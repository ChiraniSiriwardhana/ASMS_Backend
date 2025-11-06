package com.example.demo.service;


import com.example.demo.dto.AppointmentDTO;
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

    // Cancel an appointment
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
    
}
