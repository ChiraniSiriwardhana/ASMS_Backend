package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ChatHistoryDTO;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ChatbotController {
    
    private final ChatbotService chatbotService;
    
    /**
     * Send a message to the chatbot
     */
    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: {}", request.getMessage());
        try {
            ChatResponse response = chatbotService.sendMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.builder()
                            .response("Sorry, I encountered an error. Please try again.")
                            .build());
        }
    }
    
    /**
     * Get chat history for the current user
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatHistoryDTO>> getChatHistory() {
        try {
            List<ChatHistoryDTO> history = chatbotService.getChatHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error retrieving chat history", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get chat history for a specific session
     */
    @GetMapping("/history/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatHistoryDTO>> getSessionHistory(@PathVariable String sessionId) {
        try {
            List<ChatHistoryDTO> history = chatbotService.getSessionHistory(sessionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error retrieving session history", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Delete chat history for the current user
     */
    @DeleteMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteChatHistory() {
        try {
            chatbotService.deleteChatHistory();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Chat history deleted successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error deleting chat history", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to delete chat history")
                            .build());
        }
    }
}

