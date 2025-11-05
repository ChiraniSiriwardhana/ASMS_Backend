package com.example.demo.service.projects;

import com.example.demo.dto.projects.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendProjectUpdate(Long customerId, ProjectResponse project) {
        String destination = "/topic/project-updates/" + customerId;
        messagingTemplate.convertAndSend(destination, project);
    }
}