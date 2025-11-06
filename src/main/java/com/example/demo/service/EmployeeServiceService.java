package com.example.demo.service;

import com.example.demo.dto.AssignedServiceDTO;
import com.example.demo.dto.AppointmentDTO;
import com.example.demo.model.EmployeeService;
import com.example.demo.model.Service;
import com.example.demo.model.User;
import com.example.demo.repository.EmployeeServiceRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class EmployeeServiceService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeServiceRepository employeeServiceRepository;
    private final AppointmentService appointmentService;

    /**
     * Get all services assigned to an employee (by username)
     */
    @Transactional(readOnly = true)
    public List<AssignedServiceDTO> getAssignedServices(String username) {
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<EmployeeService> employeeServices = employeeServiceRepository.findByEmployeeId(employee.getId());

        return employeeServices.stream()
                .map(es -> AssignedServiceDTO.builder()
                        .serviceId(es.getService().getId())
                        .serviceName(es.getService().getServiceName())
                        .description(es.getService().getDescription())
                        .price(es.getService().getBasePrice().doubleValue())
                        .duration(es.getService().getEstimatedDuration() + " hours")
                        .assignedDate(es.getAssignedDate())
                        .assignedBy(es.getAssignedBy() != null ? es.getAssignedBy().getUsername() : "Admin")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get all appointments assigned to an employee
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAssignedAppointments(String username) {
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return appointmentService.getAppointmentsByEmployee(employee.getId());
    }

    /**
     * Admin assigns a service to an employee
     */
    @Transactional
    public void assignServiceToEmployee(Long employeeId, Long serviceId, String adminUsername) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Check if already assigned
        if (employeeServiceRepository.existsByEmployeeIdAndServiceId(employeeId, serviceId)) {
            throw new RuntimeException("Service already assigned to this employee");
        }

        EmployeeService employeeService = EmployeeService.builder()
                .employee(employee)
                .service(service)
                .assignedBy(admin)
                .build();

        employeeServiceRepository.save(employeeService);
    }

    /**
     * Admin removes a service assignment from an employee
     */
    @Transactional
    public void removeServiceFromEmployee(Long employeeId, Long serviceId) {
        EmployeeService employeeService = employeeServiceRepository
                .findByEmployeeIdAndServiceId(employeeId, serviceId)
                .orElseThrow(() -> new RuntimeException("Service assignment not found"));

        employeeServiceRepository.delete(employeeService);
    }

    /**
     * Get all services assigned to a specific employee (by employee ID)
     */
    @Transactional(readOnly = true)
    public List<AssignedServiceDTO> getServicesForEmployee(Long employeeId) {
        userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<EmployeeService> employeeServices = employeeServiceRepository.findByEmployeeId(employeeId);

        return employeeServices.stream()
                .map(es -> AssignedServiceDTO.builder()
                        .serviceId(es.getService().getId())
                        .serviceName(es.getService().getServiceName())
                        .description(es.getService().getDescription())
                        .price(es.getService().getBasePrice().doubleValue())
                        .duration(es.getService().getEstimatedDuration() + " hours")
                        .assignedDate(es.getAssignedDate())
                        .assignedBy(es.getAssignedBy() != null ? es.getAssignedBy().getUsername() : "Admin")
                        .build())
                .collect(Collectors.toList());
    }
}

