package com.example.demo.repository;

import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Custom method to find appointments by user (CUSTOMER)
    List<Appointment> findByUser(Optional<User> user);

    // Find appointments assigned to a specific employee
    List<Appointment> findByAssignedEmployeeId(Long employeeId);

    // Count appointments for a specific service on a given date (excluding cancelled/rejected)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.serviceType = :serviceType " +
           "AND CAST(a.appointmentDate AS date) = :date " +
           "AND a.status NOT IN ('CANCELLED', 'REJECTED')")
    Long countAppointmentsByServiceAndDate(@Param("serviceType") String serviceType, 
                                           @Param("date") LocalDate date);

    // Get all booked time slots for a specific date (excluding cancelled/rejected)
    @Query("SELECT a.timeSlot FROM Appointment a WHERE CAST(a.appointmentDate AS date) = :date " +
           "AND a.status NOT IN ('CANCELLED', 'REJECTED')")
    List<String> findBookedTimeSlotsByDate(@Param("date") LocalDate date);
}
