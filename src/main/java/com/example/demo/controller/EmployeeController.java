package com.example.demo.controller;

import com.example.demo.dto.AssignedServiceDTO;
import com.example.demo.dto.AppointmentDTO;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.EmployeeServiceService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    private final UserService userService;
    private final EmployeeServiceService employeeServiceService;
    private final EmployeeService employeeService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = userService.getCurrentUserProfile(username);
        return ResponseEntity.ok(user);
    }


    // Get current logged-in employee details using EmployeeService
    @GetMapping("/current")
    public ResponseEntity<UserResponse> getCurrentEmployee(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = employeeService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("Welcome to Employee Dashboard");
    }

    // Get assigned services for logged-in employee
    @GetMapping("/assigned-services")
    public ResponseEntity<List<AssignedServiceDTO>> getAssignedServices(Authentication authentication) {
        String username = authentication.getName();
        List<AssignedServiceDTO> services = employeeServiceService.getAssignedServices(username);
        return ResponseEntity.ok(services);
    }

    // Get appointments assigned to this employee
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAssignedAppointments(Authentication authentication) {
        String username = authentication.getName();
        List<AppointmentDTO> appointments = employeeServiceService.getAssignedAppointments(username);
        return ResponseEntity.ok(appointments);
    }
}

