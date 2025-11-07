package com.example.demo.config;

import com.example.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Project updates endpoint
        registry.addEndpoint("/ws/project-updates")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new JwtHandshakeHandler(jwtTokenProvider))
                .withSockJS();

        // Notifications endpoint with JWT authentication
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new JwtHandshakeHandler(jwtTokenProvider))
                .withSockJS();

        // Main WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new JwtHandshakeHandler(jwtTokenProvider))
                .withSockJS();
    }

    /**
     * Custom handshake handler to extract JWT token from query parameters
     * and authenticate WebSocket connections
     */
    private static class JwtHandshakeHandler extends DefaultHandshakeHandler {

        private final JwtTokenProvider jwtTokenProvider;

        public JwtHandshakeHandler(JwtTokenProvider jwtTokenProvider) {
            this.jwtTokenProvider = jwtTokenProvider;
        }

        @Override
        protected Principal determineUser(
                ServerHttpRequest request,
                WebSocketHandler wsHandler,
                Map<String, Object> attributes) {

            // Extract token from query parameter
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                String token = servletRequest.getServletRequest().getParameter("token");

                // Validate token and create Principal
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    Long userId = jwtTokenProvider.getUserIdFromToken(token);

                    // Store userId in attributes for later use
                    attributes.put("userId", userId);
                    attributes.put("username", username);

                    // Return Principal with username
                    return () -> username;
                }
            }

            // Return null if token is invalid or missing
            return null;
        }
    }
}