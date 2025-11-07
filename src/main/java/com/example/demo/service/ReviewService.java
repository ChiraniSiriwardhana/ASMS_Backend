package com.example.demo.service;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.model.Review;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.User;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    // ✅ Add a review
    public Review addReview(ReviewDTO dto, String username) {
        // 1️⃣ Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Validate appointment ownership & completion
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot review someone else’s appointment");
        }

        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new RuntimeException("You can only review completed appointments");
        }

        // 3️⃣ Create review using IDs (not entity objects)
        Review review = Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .appointmentId(dto.getAppointmentId())
                .userId(user.getId())
                .build();

        return reviewRepository.save(review);
    }

    // ✅ Get review by appointment
    public Optional<Review> getReviewByAppointment(Long appointmentId) {
        return reviewRepository.findByAppointmentId(appointmentId);
    }

    // ✅ Update review
    public Review updateReview(Long id, int rating, String comment) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    // ✅ Delete review
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
