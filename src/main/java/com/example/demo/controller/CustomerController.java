package com.example.demo.controller;

import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.ServiceResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.Appointment;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AppointmentDTO;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.ServiceManagementService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final ServiceManagementService serviceManagementService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = userService.getCurrentUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserResponse updatedUser = userService.updateProfile(username, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("Welcome to Customer Dashboard");
    }

    // Get all active services (public access for booking - configured in SecurityConfig)
    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getActiveServices() {
        List<ServiceResponse> services = serviceManagementService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // Create an appointment
    @PostMapping("/appointments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO appointmentDTO, Authentication authentication) {
        // Get the logged-in user from Authentication object
        String username = authentication.getName();
        Appointment appointment = appointmentService.createAppointment(appointmentDTO, username);
        return ResponseEntity.ok(appointment);
    }

    // Get all appointments for the logged-in customer
    @GetMapping("/appointments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Appointment>> getCustomerAppointments(Authentication authentication) {
        String username = authentication.getName();
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(username);
        return ResponseEntity.ok(appointments);
    }

    // Get appointment status for a specific appointment (optional)
    @GetMapping("/appointments/{appointmentId}/status")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> getAppointmentStatus(@PathVariable Long appointmentId) {
        String status = appointmentService.getAppointmentStatus(appointmentId);
        return ResponseEntity.ok(status);
    }

    //Cancel an appointment
    @PutMapping("/appointments/{appointmentId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        appointmentService.cancelAppointment(appointmentId, username);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully"));
    }

    // Get booked time slots for a specific date (PUBLIC - no authentication required)
    @GetMapping("/appointments/booked-slots")
    public ResponseEntity<List<String>> getBookedTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<String> bookedSlots = appointmentService.getBookedTimeSlots(date);
        return ResponseEntity.ok(bookedSlots);
    }
}

