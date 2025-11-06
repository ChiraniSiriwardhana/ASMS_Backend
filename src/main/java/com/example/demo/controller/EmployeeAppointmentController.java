package com.example.demo.controller;

import com.example.demo.dto.AppointmentStatusUpdateDTO;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"},
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
             allowCredentials = "true")
public class EmployeeAppointmentController {

    private final AppointmentRepository appointmentRepository;

    /**
     * Update appointment status by employee
     * PUT /api/employee/appointments/{id}/status
     *
     * Valid status values: PENDING, CONFIRMED, IN_SERVICE, READY, COMPLETED, CANCELLED
     */
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody AppointmentStatusUpdateDTO updateDTO) {

        try {
            // Find appointment
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

            // Update status using existing enum
            if (updateDTO.getStatus() != null) {
                appointment.setStatus(updateDTO.getStatus());
            }

            // Save changes to database - Customer will see this when they query their appointments
            // updatedAt timestamp will be automatically set by @UpdateTimestamp
            Appointment updated = appointmentRepository.save(appointment);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment status updated successfully");
            response.put("appointment", updated);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Alternative endpoint: PATCH method for status update
     * PATCH /api/employee/appointments/{id}
     *
     * Body example: { "status": "COMPLETED" }
     */
    @PatchMapping("/appointments/{id}")
    public ResponseEntity<?> patchAppointmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {

        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

            // Update status if provided
            if (updates.containsKey("status")) {
                String statusStr = updates.get("status");
                AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());
                appointment.setStatus(status);
            }

            // Save to database - updatedAt will be automatically set
            Appointment updated = appointmentRepository.save(appointment);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment status updated successfully");
            response.put("appointment", updated);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", "Invalid status. Valid values: PENDING, CONFIRMED, IN_SERVICE, READY, COMPLETED, CANCELLED");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get appointment details for employee
     * GET /api/employee/appointments/{id}
     */
    @GetMapping("/appointments/{id}")
    public ResponseEntity<?> getAppointmentDetails(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

            return ResponseEntity.ok(appointment);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

