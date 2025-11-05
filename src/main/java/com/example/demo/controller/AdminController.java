package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.ServiceRequest;
import com.example.demo.dto.ServiceResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.AdminService;
import com.example.demo.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ServiceManagementService serviceManagementService;

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
}
