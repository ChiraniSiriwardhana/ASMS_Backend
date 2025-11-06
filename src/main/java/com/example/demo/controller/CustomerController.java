package com.example.demo.controller;

import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.Appointment;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AppointmentDTO;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    private final UserService userService;
    private final AppointmentService appointmentService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = userService.getCurrentUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserResponse updatedUser = userService.updateProfile(username, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("Welcome to Customer Dashboard");
    }

    // Create an appointment
    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO appointmentDTO, Authentication authentication) {
        // Get the logged-in user from Authentication object
        String username = authentication.getName();
        Appointment appointment = appointmentService.createAppointment(appointmentDTO, username);
        return ResponseEntity.ok(appointment);
    }

    // Get all appointments for the logged-in customer
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getCustomerAppointments(Authentication authentication) {
        String username = authentication.getName();
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(username);
        return ResponseEntity.ok(appointments);
    }

    // Get appointment status for a specific appointment (optional)
    @GetMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<String> getAppointmentStatus(@PathVariable Long appointmentId) {
        String status = appointmentService.getAppointmentStatus(appointmentId);
        return ResponseEntity.ok(status);
    }

    //Cancel an appointment
    @PutMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        appointmentService.cancelAppointment(appointmentId, username);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully"));
    }
}

