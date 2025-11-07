package com.example.demo.controller;

import com.example.demo.dto.CustomerQuestionRequest;
import com.example.demo.model.CustomerQuestion;
import com.example.demo.service.CustomerQuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/questions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3004"})
public class CustomerQuestionController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerQuestionController.class);

    @Autowired
    private CustomerQuestionService customerQuestionService;

    // Submit a new question (public endpoint)
    @PostMapping
    public ResponseEntity<?> submitQuestion(@Valid @RequestBody CustomerQuestionRequest request) {
        try {
            logger.info("Received question submission: {}", request);
            CustomerQuestion question = customerQuestionService.createQuestion(request);
            logger.info("Question created successfully with ID: {}", question.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(question);
        } catch (Exception e) {
            logger.error("Error submitting question: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to submit question");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }
}
