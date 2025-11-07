package com.example.demo.controller;

import com.example.demo.dto.AppointmentStatusUpdateDTO;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.NotificationType;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
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
    private final NotificationService notificationService;
    private final UserRepository userRepository;

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

            AppointmentStatus oldStatus = appointment.getStatus();

            // Update status using existing enum
            if (updateDTO.getStatus() != null) {
                appointment.setStatus(updateDTO.getStatus());
            }

            // Save changes to database - Customer will see this when they query their appointments
            // updatedAt timestamp will be automatically set by @UpdateTimestamp
            Appointment updated = appointmentRepository.save(appointment);

            // Send notifications based on status change
            sendStatusChangeNotifications(updated, oldStatus);

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

            AppointmentStatus oldStatus = appointment.getStatus();

            // Update status if provided
            if (updates.containsKey("status")) {
                String statusStr = updates.get("status");
                AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());
                appointment.setStatus(status);
            }

            // Save to database - updatedAt will be automatically set
            Appointment updated = appointmentRepository.save(appointment);

            // Send notifications based on status change
            sendStatusChangeNotifications(updated, oldStatus);

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

    /**
     * Helper method to send notifications based on status changes
     */
    private void sendStatusChangeNotifications(Appointment appointment, AppointmentStatus oldStatus) {
        User customer = appointment.getUser();
        AppointmentStatus newStatus = appointment.getStatus();

        // Only send notification if status actually changed
        if (oldStatus == newStatus) {
            return;
        }

        String vehicleInfo = appointment.getVehicleBrand() + " " + appointment.getModel();

        switch (newStatus) {
            case IN_SERVICE:
                notificationService.notifyCustomer(
                        customer.getId(),
                        appointment.getId(),
                        "Service Started",
                        "Your " + vehicleInfo + " service has been started.",
                        NotificationType.STATUS_CHANGED_IN_SERVICE
                );
                notificationService.notifyAdmins(
                        appointment.getId(),
                        "Service Started",
                        "Service started for " + vehicleInfo + " (Customer: " + customer.getFirstName() + " " + customer.getLastName() + ")",
                        NotificationType.STATUS_CHANGED_IN_SERVICE
                );
                break;

            case READY:
                notificationService.notifyCustomer(
                        customer.getId(),
                        appointment.getId(),
                        "Vehicle Ready for Pickup",
                        "Good news! Your " + vehicleInfo + " is ready for pickup.",
                        NotificationType.STATUS_CHANGED_READY
                );
                notificationService.notifyAdmins(
                        appointment.getId(),
                        "Service Ready",
                        vehicleInfo + " is ready for pickup (Customer: " + customer.getFirstName() + " " + customer.getLastName() + ")",
                        NotificationType.STATUS_CHANGED_READY
                );
                break;

            case COMPLETED:
                notificationService.notifyCustomer(
                        customer.getId(),
                        appointment.getId(),
                        "Service Completed",
                        "Your " + vehicleInfo + " service has been completed successfully. Thank you for choosing us!",
                        NotificationType.STATUS_CHANGED_COMPLETED
                );
                notificationService.notifyAdmins(
                        appointment.getId(),
                        "Service Completed",
                        "Service completed for " + vehicleInfo + " (Customer: " + customer.getFirstName() + " " + customer.getLastName() + ")",
                        NotificationType.STATUS_CHANGED_COMPLETED
                );
                break;

            case CANCELLED:
                notificationService.notifyCustomer(
                        customer.getId(),
                        appointment.getId(),
                        "Appointment Cancelled",
                        "Your appointment for " + vehicleInfo + " has been cancelled.",
                        NotificationType.APPOINTMENT_CANCELLED
                );
                notificationService.notifyAdmins(
                        appointment.getId(),
                        "Appointment Cancelled",
                        "Appointment cancelled for " + vehicleInfo,
                        NotificationType.APPOINTMENT_CANCELLED
                );
                break;

            default:
                // For other status changes, send a general notification
                notificationService.notifyCustomer(
                        customer.getId(),
                        appointment.getId(),
                        "Appointment Status Updated",
                        "Your appointment for " + vehicleInfo + " status has been updated to " + newStatus,
                        NotificationType.GENERAL
                );
                break;
        }
    }
}

