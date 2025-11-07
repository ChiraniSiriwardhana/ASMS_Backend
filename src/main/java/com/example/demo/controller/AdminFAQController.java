package com.example.demo.controller;

import com.example.demo.dto.FAQRequest;
import com.example.demo.model.FAQ;
import com.example.demo.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/faqs")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3004"})
public class AdminFAQController {

    @Autowired
    private FAQService faqService;

    // Get all FAQs (including inactive)
    @GetMapping
    public ResponseEntity<List<FAQ>> getAllFAQs() {
        List<FAQ> faqs = faqService.getAllFAQs();
        return ResponseEntity.ok(faqs);
    }

    // Get FAQ by ID
    @GetMapping("/{id}")
    public ResponseEntity<FAQ> getFAQById(@PathVariable Long id) {
        return faqService.getFAQById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new FAQ
    @PostMapping
    public ResponseEntity<FAQ> createFAQ(@RequestBody FAQRequest request) {
        try {
            FAQ faq = faqService.createFAQ(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(faq);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update FAQ
    @PutMapping("/{id}")
    public ResponseEntity<FAQ> updateFAQ(@PathVariable Long id, @RequestBody FAQRequest request) {
        try {
            FAQ faq = faqService.updateFAQ(id, request);
            return ResponseEntity.ok(faq);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Toggle FAQ status
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<FAQ> toggleFAQStatus(@PathVariable Long id) {
        try {
            FAQ faq = faqService.toggleFAQStatus(id);
            return ResponseEntity.ok(faq);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete FAQ
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFAQ(@PathVariable Long id) {
        try {
            faqService.deleteFAQ(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "FAQ deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
