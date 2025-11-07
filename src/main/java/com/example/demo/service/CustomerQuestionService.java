package com.example.demo.service;

import com.example.demo.dto.AnswerQuestionRequest;
import com.example.demo.dto.CustomerQuestionRequest;
import com.example.demo.model.CustomerQuestion;
import com.example.demo.repository.CustomerQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerQuestionService {

    @Autowired
    private CustomerQuestionRepository customerQuestionRepository;

    // Get all customer questions
    public List<CustomerQuestion> getAllQuestions() {
        return customerQuestionRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get questions by resolved status
    public List<CustomerQuestion> getQuestionsByStatus(Boolean isResolved) {
        return customerQuestionRepository.findByIsResolvedOrderByCreatedAtDesc(isResolved);
    }
    
    // Get answered questions (questions with answers)
    public List<CustomerQuestion> getAnsweredQuestions() {
        return customerQuestionRepository.findAll().stream()
                .filter(q -> q.getAnswer() != null && !q.getAnswer().trim().isEmpty())
                .sorted((q1, q2) -> q2.getAnsweredAt().compareTo(q1.getAnsweredAt()))
                .toList();
    }
    
    // Get question by email
    public List<CustomerQuestion> getQuestionsByEmail(String email) {
        return customerQuestionRepository.findAll().stream()
                .filter(q -> q.getEmail().equalsIgnoreCase(email))
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .toList();
    }

    // Get question by ID
    public Optional<CustomerQuestion> getQuestionById(Long id) {
        return customerQuestionRepository.findById(id);
    }

    // Create new customer question
    @Transactional
    public CustomerQuestion createQuestion(CustomerQuestionRequest request) {
        CustomerQuestion question = new CustomerQuestion();
        question.setFullName(request.getFullName());
        question.setEmail(request.getEmail());
        question.setCategory(request.getCategory());
        question.setQuestion(request.getQuestion());
        question.setAttachmentUrl(request.getAttachmentUrl());
        question.setIsResolved(false);
        return customerQuestionRepository.save(question);
    }

    // Mark question as resolved
    @Transactional
    public CustomerQuestion markAsResolved(Long id) {
        CustomerQuestion question = customerQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        
        question.setIsResolved(true);
        return customerQuestionRepository.save(question);
    }

    // Answer a customer question
    @Transactional
    public CustomerQuestion answerQuestion(Long id, AnswerQuestionRequest request) {
        CustomerQuestion question = customerQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        
        question.setAnswer(request.getAnswer());
        question.setAnsweredAt(LocalDateTime.now());
        question.setIsResolved(true);
        return customerQuestionRepository.save(question);
    }

    // Mark question as unresolved
    @Transactional
    public CustomerQuestion markAsUnresolved(Long id) {
        CustomerQuestion question = customerQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        
        question.setIsResolved(false);
        return customerQuestionRepository.save(question);
    }

    // Delete question
    @Transactional
    public void deleteQuestion(Long id) {
        if (!customerQuestionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }
        customerQuestionRepository.deleteById(id);
    }
}
