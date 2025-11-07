package com.example.demo.controller;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class ReviewController {

    private final ReviewService reviewService;

    // 🟢 Add a review
    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO) {
        // Get logged-in username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Review saved = reviewService.addReview(reviewDTO, username);
        return ResponseEntity.ok(saved);
    }

    // 🟡 Get review for an appointment
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getReview(@PathVariable Long appointmentId) {
        return reviewService.getReviewByAppointment(appointmentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔵 Update review
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        Review review = reviewService.updateReview(id, updatedReview.getRating(), updatedReview.getComment());
        return ResponseEntity.ok(review);
    }

    // 🔴 Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted");
    }
}
