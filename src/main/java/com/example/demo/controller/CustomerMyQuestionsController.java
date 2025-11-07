package com.example.demo.controller;

import com.example.demo.model.CustomerQuestion;
import com.example.demo.service.CustomerQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/my-questions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3004"})
public class CustomerMyQuestionsController {

    @Autowired
    private CustomerQuestionService customerQuestionService;

    // Get all answered questions (public - to display as FAQs)
    @GetMapping("/answered")
    public ResponseEntity<List<CustomerQuestion>> getAnsweredQuestions() {
        List<CustomerQuestion> questions = customerQuestionService.getAnsweredQuestions();
        return ResponseEntity.ok(questions);
    }

    // Get questions by email (for customer to track their own questions)
    @GetMapping("/by-email")
    public ResponseEntity<List<CustomerQuestion>> getQuestionsByEmail(@RequestParam String email) {
        List<CustomerQuestion> questions = customerQuestionService.getQuestionsByEmail(email);
        return ResponseEntity.ok(questions);
    }
}
