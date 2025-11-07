package com.example.demo.controller;

import com.example.demo.model.FAQ;
import com.example.demo.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/faqs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3004"})
public class CustomerFAQController {

    @Autowired
    private FAQService faqService;

    // Get all active FAQs for customers (public endpoint)
    @GetMapping
    public ResponseEntity<List<FAQ>> getAllActiveFAQs() {
        List<FAQ> faqs = faqService.getAllActiveFAQs();
        return ResponseEntity.ok(faqs);
    }

    // Get FAQ by ID (public endpoint)
    @GetMapping("/{id}")
    public ResponseEntity<FAQ> getFAQById(@PathVariable Long id) {
        return faqService.getFAQById(id)
                .filter(FAQ::getIsActive) // Only return if active
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
