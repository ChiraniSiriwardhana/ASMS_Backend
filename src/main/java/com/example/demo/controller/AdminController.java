package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AssignedServiceDTO;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.ServiceRequest;
import com.example.demo.dto.ServiceResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.AppointmentAdminResponse;
import com.example.demo.model.Appointment;
import com.example.demo.service.AdminService;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.EmployeeServiceService;
import com.example.demo.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ServiceManagementService serviceManagementService;
    private final AppointmentService appointmentService;
    private final EmployeeServiceService employeeServiceService;

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse> addEmployee(@Valid @RequestBody EmployeeRequest request) {
        ApiResponse response = adminService.addEmployee(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getAllEmployees() {
        List<UserResponse> employees = adminService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        List<UserResponse> customers = adminService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long id) {
        ApiResponse response = adminService.deactivateUser(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long id) {
        ApiResponse response = adminService.activateUser(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/employees/{id}/resend-activation")
    public ResponseEntity<ApiResponse> resendActivationEmail(@PathVariable Long id) {
        ApiResponse response = adminService.resendEmployeeActivationEmail(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        ApiResponse response = adminService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) {
        ApiResponse response = adminService.deleteEmployee(id);
        return ResponseEntity.ok(response);
    }

    // Service Management Endpoints
    @PostMapping("/services")
    public ResponseEntity<ApiResponse> createService(@Valid @RequestBody ServiceRequest request) {
        ApiResponse response = serviceManagementService.createService(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<ServiceResponse> services = serviceManagementService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        ServiceResponse service = serviceManagementService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ApiResponse> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        ApiResponse response = serviceManagementService.updateService(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/services/{id}/activate")
    public ResponseEntity<ApiResponse> activateService(@PathVariable Long id) {
        ApiResponse response = serviceManagementService.activateService(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/services/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateService(@PathVariable Long id) {
        ApiResponse response = serviceManagementService.deactivateService(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<ApiResponse> deleteService(@PathVariable Long id) {
        ApiResponse response = serviceManagementService.deleteService(id);
        return ResponseEntity.ok(response);
    }

    // Profile Management
    @PutMapping("/profile/update")
    public ResponseEntity<UserResponse> updateAdminProfile(@Valid @RequestBody com.example.demo.dto.ProfileUpdateRequest request,
                                                           @RequestHeader("Authorization") String token) {
        UserResponse response = adminService.updateAdminProfile(request, token);
        return ResponseEntity.ok(response);
    }

    // Appointment Management
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentAdminResponse>> getAllAppointments() {
        List<AppointmentAdminResponse> appointments = appointmentService.getAllAppointmentsWithCustomerDetails();
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointments/{id}/approve")
    public ResponseEntity<Appointment> approveAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.approveAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/appointments/{id}/reject")
    public ResponseEntity<Appointment> rejectAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.rejectAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/appointments/{id}/assign/{employeeId}")
    public ResponseEntity<Appointment> assignEmployeeToAppointment(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        Appointment appointment = appointmentService.assignEmployeeToAppointment(id, employeeId);
        return ResponseEntity.ok(appointment);
    }

    // Employee Service Assignment Endpoints

    // Assign service to employee
    @PostMapping("/employees/{employeeId}/assign-service/{serviceId}")
    public ResponseEntity<ApiResponse> assignServiceToEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long serviceId,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        employeeServiceService.assignServiceToEmployee(employeeId, serviceId, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("Service assigned to employee successfully"));
    }

    // Remove service from employee
    @DeleteMapping("/employees/{employeeId}/remove-service/{serviceId}")
    public ResponseEntity<ApiResponse> removeServiceFromEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long serviceId
    ) {
        employeeServiceService.removeServiceFromEmployee(employeeId, serviceId);
        return ResponseEntity.ok(ApiResponse.success("Service removed from employee successfully"));
    }

    // Get all services assigned to a specific employee
    @GetMapping("/employees/{employeeId}/services")
    public ResponseEntity<List<AssignedServiceDTO>> getEmployeeServices(@PathVariable Long employeeId) {
        List<AssignedServiceDTO> services = employeeServiceService.getServicesForEmployee(employeeId);
        return ResponseEntity.ok(services);
    }
}
