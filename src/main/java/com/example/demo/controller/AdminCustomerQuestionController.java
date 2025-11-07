package com.example.demo.controller;

import com.example.demo.dto.AnswerQuestionRequest;
import com.example.demo.dto.CustomerQuestionRequest;
import com.example.demo.model.CustomerQuestion;
import com.example.demo.service.CustomerQuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/customer-questions")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3004"})
public class AdminCustomerQuestionController {

    @Autowired
    private CustomerQuestionService customerQuestionService;

    // Get all customer questions
    @GetMapping
    public ResponseEntity<List<CustomerQuestion>> getAllQuestions() {
        List<CustomerQuestion> questions = customerQuestionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // Get questions by resolved status
    @GetMapping("/status/{isResolved}")
    public ResponseEntity<List<CustomerQuestion>> getQuestionsByStatus(@PathVariable Boolean isResolved) {
        List<CustomerQuestion> questions = customerQuestionService.getQuestionsByStatus(isResolved);
        return ResponseEntity.ok(questions);
    }

    // Get question by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerQuestion> getQuestionById(@PathVariable Long id) {
        return customerQuestionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mark question as resolved
    @PutMapping("/{id}/resolve")
    public ResponseEntity<CustomerQuestion> markAsResolved(@PathVariable Long id) {
        try {
            CustomerQuestion question = customerQuestionService.markAsResolved(id);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Answer a customer question
    @PutMapping("/{id}/answer")
    public ResponseEntity<CustomerQuestion> answerQuestion(
            @PathVariable Long id,
            @Valid @RequestBody AnswerQuestionRequest request) {
        try {
            CustomerQuestion question = customerQuestionService.answerQuestion(id, request);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mark question as unresolved
    @PutMapping("/{id}/unresolve")
    public ResponseEntity<CustomerQuestion> markAsUnresolved(@PathVariable Long id) {
        try {
            CustomerQuestion question = customerQuestionService.markAsUnresolved(id);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete question
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long id) {
        try {
            customerQuestionService.deleteQuestion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Question deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
