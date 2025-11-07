package com.example.demo.repository;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUserOrderByTimestampDesc(User user);

    List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId);

    List<ChatMessage> findByUserAndSessionIdOrderByTimestampAsc(User user, String sessionId);

    void deleteByUser(User user);
}

