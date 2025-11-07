package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    @Value("${chatbot.test.mode:false}")
    private boolean testMode;

    /**
     * Send a message to the chatbot and get a response
     */
    @Transactional
    public ChatResponse sendMessage(ChatRequest request) {
        log.info("Processing chat message: {}", request.getMessage());

        User user;
        try {
            user = getCurrentUser();
            log.info("User found: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get current user: " + e.getMessage(), e);
        }

        // Generate session ID if not provided
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        // TEST MODE: Return mock responses without calling external API
        if (testMode) {
            try {
                log.info("TEST MODE: Returning mock response for user: {}", user.getEmail());
                String mockResponse = generateMockResponse(request.getMessage());

                // Save to database
                ChatMessage chatMessage = ChatMessage.builder()
                        .user(user)
                        .sessionId(sessionId)
                        .message(request.getMessage())
                        .response(mockResponse)
                        .tokensUsed(0)
                        .timestamp(LocalDateTime.now())
                        .build();

                chatMessageRepository.save(chatMessage);
                log.info("Chat message saved successfully");

                return ChatResponse.builder()
                        .response(mockResponse)
                        .sessionId(sessionId)
                        .timestamp(LocalDateTime.now())
                        .tokensUsed(0)
                        .build();
            } catch (Exception e) {
                log.error("Error in test mode: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to process chat message in test mode: " + e.getMessage(), e);
            }
        }

        // Get chat history for context (last 10 messages from this session)
        List<ChatMessage> history = chatMessageRepository
                .findByUserAndSessionIdOrderByTimestampAsc(user, sessionId)
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        // Build messages for OpenRouter API
        List<OpenRouterRequest.Message> messages = new ArrayList<>();

        // Add system message
        messages.add(OpenRouterRequest.Message.builder()
                .role("system")
                .content("You are a helpful assistant for ASMS (Automotive Service Management System). Provide clear, concise, and accurate responses about vehicle services, appointments, and automotive maintenance.")
                .build());

        // Add conversation history
        for (ChatMessage msg : history) {
            messages.add(OpenRouterRequest.Message.builder()
                    .role("user")
                    .content(msg.getMessage())
                    .build());
            messages.add(OpenRouterRequest.Message.builder()
                    .role("assistant")
                    .content(msg.getResponse())
                    .build());
        }

        // Add current message
        messages.add(OpenRouterRequest.Message.builder()
                .role("user")
                .content(request.getMessage())
                .build());

        // Prepare OpenRouter request
        OpenRouterRequest openRouterRequest = OpenRouterRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        // Call OpenRouter API
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");
            headers.set("HTTP-Referer", "http://localhost:8080");
            headers.set("X-Title", "ASMS Chatbot");

            HttpEntity<OpenRouterRequest> entity = new HttpEntity<>(openRouterRequest, headers);

            log.info("Sending request to OpenRouter API: {}", apiUrl);
            ResponseEntity<OpenRouterResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    OpenRouterResponse.class
            );

            if (response.getBody() == null || response.getBody().getChoices() == null || response.getBody().getChoices().isEmpty()) {
                throw new RuntimeException("Empty response from OpenRouter API");
            }

            String aiResponse = response.getBody().getChoices().get(0).getMessage().getContent();
            Integer tokensUsed = response.getBody().getUsage() != null ?
                    response.getBody().getUsage().getTotalTokens() : null;

            // Save to database
            ChatMessage chatMessage = ChatMessage.builder()
                    .user(user)
                    .sessionId(sessionId)
                    .message(request.getMessage())
                    .response(aiResponse)
                    .tokensUsed(tokensUsed)
                    .timestamp(LocalDateTime.now())
                    .build();

            chatMessageRepository.save(chatMessage);

            return ChatResponse.builder()
                    .response(aiResponse)
                    .sessionId(sessionId)
                    .timestamp(LocalDateTime.now())
                    .tokensUsed(tokensUsed)
                    .build();

        } catch (Exception e) {
            log.error("Error calling OpenRouter API: {}", e.getMessage(), e);

            // Provide more specific error messages
            String errorMessage;
            if (e.getMessage() != null) {
                if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                    errorMessage = "API authentication failed. Please check your OpenRouter API key.";
                } else if (e.getMessage().contains("429")) {
                    errorMessage = "API rate limit exceeded. Please try again later.";
                } else if (e.getMessage().contains("timeout") || e.getMessage().contains("Connection")) {
                    errorMessage = "Connection to AI service timed out. Please check your internet connection.";
                } else if (e.getMessage().contains("Empty response")) {
                    errorMessage = "AI service returned an empty response. The model might be unavailable.";
                } else {
                    errorMessage = "Failed to get response from chatbot: " + e.getMessage();
                }
            } else {
                errorMessage = "An unexpected error occurred while contacting the AI service.";
            }

            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Get chat history for the current user
     */
    public List<ChatHistoryDTO> getChatHistory() {
        User user = getCurrentUser();
        List<ChatMessage> messages = chatMessageRepository.findByUserOrderByTimestampDesc(user);

        return messages.stream()
                .map(msg -> ChatHistoryDTO.builder()
                        .id(msg.getId())
                        .message(msg.getMessage())
                        .response(msg.getResponse())
                        .timestamp(msg.getTimestamp())
                        .sessionId(msg.getSessionId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get chat history for a specific session
     */
    public List<ChatHistoryDTO> getSessionHistory(String sessionId) {
        User user = getCurrentUser();
        List<ChatMessage> messages = chatMessageRepository
                .findByUserAndSessionIdOrderByTimestampAsc(user, sessionId);

        return messages.stream()
                .map(msg -> ChatHistoryDTO.builder()
                        .id(msg.getId())
                        .message(msg.getMessage())
                        .response(msg.getResponse())
                        .timestamp(msg.getTimestamp())
                        .sessionId(msg.getSessionId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Delete chat history for the current user
     */
    @Transactional
    public void deleteChatHistory() {
        User user = getCurrentUser();
        chatMessageRepository.deleteByUser(user);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                log.error("No authentication found in security context");
                throw new RuntimeException("User not authenticated");
            }

            String username = authentication.getName();
            log.info("Getting current user with username: {}", username);

            // Try to find by username first, then by email
            return userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(username))
                    .orElseThrow(() -> {
                        log.error("User not found with username/email: {}", username);
                        return new RuntimeException("User not found: " + username);
                    });
        } catch (Exception e) {
            log.error("Error getting current user", e);
            throw new RuntimeException("Failed to get current user: " + e.getMessage(), e);
        }
    }

    /**
     * Generate mock response for testing without external API
     */
    private String generateMockResponse(String message) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("hello") || lowerMessage.contains("hi") || lowerMessage.contains("hey")) {
            return "Hello! I'm the ASMS chatbot assistant. I can help you with:\n\n" +
                   "• Vehicle service information\n" +
                   "• Booking appointments\n" +
                   "• Business hours and location\n" +
                   "• Pricing details\n\n" +
                   "What would you like to know? (Running in TEST MODE)";
        } else if (lowerMessage.contains("service") || lowerMessage.contains("what")) {
            return "ASMS (Automotive Service Management System) provides comprehensive vehicle service management:\n\n" +
                   "✓ Regular maintenance scheduling\n" +
                   "✓ Complete repair services\n" +
                   "✓ Genuine parts availability\n" +
                   "✓ Expert technicians\n" +
                   "✓ Digital service history\n" +
                   "✓ Customer portal access\n\n" +
                   "Which service are you interested in?";
        } else if (lowerMessage.contains("appointment") || lowerMessage.contains("book")) {
            return "Booking an appointment is easy! Follow these steps:\n\n" +
                   "1. Visit our appointments section\n" +
                   "2. Choose your preferred date and time\n" +
                   "3. Select service type (oil change, brake service, etc.)\n" +
                   "4. Provide your vehicle details\n" +
                   "5. Confirm your booking\n\n" +
                   "You'll receive a confirmation email immediately!";
        } else if (lowerMessage.contains("hour") || lowerMessage.contains("time") || lowerMessage.contains("open")) {
            return "🕐 ASMS Service Center Hours:\n\n" +
                   "Monday - Friday: 8:00 AM - 6:00 PM\n" +
                   "Saturday: 9:00 AM - 4:00 PM\n" +
                   "Sunday: Closed\n\n" +
                   "📍 Location: [Your Address Here]\n" +
                   "📞 Phone: [Your Phone Number]\n\n" +
                   "We're here to help!";
        } else if (lowerMessage.contains("price") || lowerMessage.contains("cost") || lowerMessage.contains("how much")) {
            return "💰 Our Competitive Pricing:\n\n" +
                   "• Basic Oil Change: $50-$75\n" +
                   "• Tire Rotation: $35-$50\n" +
                   "• Brake Service: $150-$300\n" +
                   "• Full Vehicle Inspection: $100-$150\n" +
                   "• Air Filter Replacement: $25-$50\n\n" +
                   "For a detailed quote specific to your vehicle, please contact us or book an appointment!";
        } else if (lowerMessage.contains("contact") || lowerMessage.contains("phone") || lowerMessage.contains("email")) {
            return "📞 Contact ASMS:\n\n" +
                   "Phone: [Your Phone Number]\n" +
                   "Email: [Your Email]\n" +
                   "Address: [Your Address]\n\n" +
                   "🌐 You can also reach us through our customer portal or by booking an appointment online!";
        } else {
            return "Thank you for your message! I'm here to assist you with:\n\n" +
                   "• Service information and booking\n" +
                   "• Business hours and location\n" +
                   "• Pricing and estimates\n" +
                   "• General automotive questions\n\n" +
                   "Feel free to ask me anything! (Currently running in TEST MODE with mock responses)";
        }
    }
}

