package com.example.demo.service;


import com.example.demo.dto.AppointmentDTO;
import com.example.demo.dto.AppointmentAdminResponse;
import com.example.demo.model.Appointment;
import com.example.demo.model.AppointmentStatus;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
    
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Create an appointment
    public Appointment createAppointment(AppointmentDTO appointmentDTO, String username) {
        // Get the logged-in user by username, throwing an exception if the user is not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Appointment appointment = new Appointment();
        appointment.setVehicleType(appointmentDTO.getVehicleType());
        appointment.setVehicleBrand(appointmentDTO.getVehicleBrand());
        appointment.setModel(appointmentDTO.getModel());
        appointment.setYearOfManufacture(appointmentDTO.getYearOfManufacture());
        appointment.setRegisterNumber(appointmentDTO.getRegisterNumber());
        appointment.setFuelType(appointmentDTO.getFuelType());
        appointment.setServiceCategory(appointmentDTO.getServiceCategory());
        appointment.setServiceType(appointmentDTO.getServiceType());
        appointment.setAdditionalRequirements(appointmentDTO.getAdditionalRequirements());
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setTimeSlot(appointmentDTO.getTimeSlot());
        appointment.setStatus(AppointmentStatus.PENDING);  // Default to Pending
        appointment.setUser(user);  // Assign the logged-in user to the appointment

        return appointmentRepository.save(appointment);
    }

    // Get all appointments for the logged-in customer
    public List<Appointment> getAppointmentsByCustomer(String username) {
        // Get the logged-in user by username
        Optional<User> user = userRepository.findByUsername(username);

        // Fetch all appointments associated with this user
        return appointmentRepository.findByUser(user);  // Custom method to find appointments by user
    }

    // Optional: Get status of a specific appointment
    public String getAppointmentStatus(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        return appointment.getStatus().name();  // Return the status as a string
    }

    // Admin: Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Admin: Get all appointments with customer details
    public List<AppointmentAdminResponse> getAllAppointmentsWithCustomerDetails() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());
    }

    // Convert Appointment to AppointmentAdminResponse with customer details
    private AppointmentAdminResponse convertToAdminResponse(Appointment appointment) {
        User customer = appointment.getUser();
        
        // Get assigned employee name if exists
        String assignedEmployeeName = null;
        if (appointment.getAssignedEmployeeId() != null) {
            Optional<User> employee = userRepository.findById(appointment.getAssignedEmployeeId());
            if (employee.isPresent()) {
                User emp = employee.get();
                assignedEmployeeName = emp.getFirstName() + " " + emp.getLastName();
            }
        }
        
        return AppointmentAdminResponse.builder()
                .id(appointment.getId())
                .vehicleType(appointment.getVehicleType())
                .vehicleBrand(appointment.getVehicleBrand())
                .model(appointment.getModel())
                .yearOfManufacture(appointment.getYearOfManufacture())
                .registerNumber(appointment.getRegisterNumber())
                .fuelType(appointment.getFuelType())
                .serviceCategory(appointment.getServiceCategory())
                .serviceType(appointment.getServiceType())
                .additionalRequirements(appointment.getAdditionalRequirements())
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlot(appointment.getTimeSlot())
                .status(appointment.getStatus())
                .customerUsername(customer.getUsername())
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhoneNumber())
                .customerFirstName(customer.getFirstName())
                .customerLastName(customer.getLastName())
                .assignedEmployeeId(appointment.getAssignedEmployeeId())
                .assignedEmployeeName(assignedEmployeeName)
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    // Admin: Approve appointment (change status from PENDING to CONFIRMED)
    public Appointment approveAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be approved");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    // Admin: Reject appointment (change status to CANCELLED)
    public Appointment rejectAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    // Admin: Assign employee to appointment (change status to IN_SERVICE)
    public Appointment assignEmployeeToAppointment(Long appointmentId, Long employeeId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Verify the employee exists
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Assign the employee
        appointment.setAssignedEmployeeId(employeeId);
        
        // Change status to IN_SERVICE
        appointment.setStatus(AppointmentStatus.IN_SERVICE);
        
        return appointmentRepository.save(appointment);
    }

    // Customer: Cancel an appointment
    public void cancelAppointment(Long appointmentId, String username) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Ensure only the owner can cancel their appointment
        if (!appointment.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You are not authorized to cancel this appointment");
        }

        // Prevent cancelling if already cancelled or completed
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment is already cancelled");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Completed appointments cannot be cancelled");
        }

        // Update status
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    // Employee: Get appointments assigned to employee
    public List<AppointmentDTO> getAppointmentsByEmployee(Long employeeId) {
        List<Appointment> appointments = appointmentRepository.findByAssignedEmployeeId(employeeId);

        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert Appointment to AppointmentDTO
    private AppointmentDTO convertToDTO(Appointment appointment) {
        User customer = appointment.getUser();

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .customerId(customer.getId())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .serviceId(null) // If you have service ID in appointment
                .serviceName(appointment.getServiceType())
                .employeeId(appointment.getAssignedEmployeeId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus().name())
                .notes(appointment.getAdditionalRequirements())
                .build();
    }
}
