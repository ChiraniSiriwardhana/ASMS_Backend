package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ServiceRequest;
import com.example.demo.dto.ServiceResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public ApiResponse createService(ServiceRequest request) {
        com.example.demo.model.Service service = com.example.demo.model.Service.builder()
                .serviceName(request.getServiceName())
                .category(request.getCategory())
                .description(request.getDescription())
                .estimatedDuration(request.getEstimatedDuration())
                .basePrice(request.getBasePrice())
                .requiredSkills(request.getRequiredSkills())
                .priority(request.getPriority())
                .maxDailySlots(request.getMaxDailySlots())
                .availableSlots(request.getMaxDailySlots())
                .serviceImage(request.getServiceImage())
                .isActive(true)
                .additionalNotes(request.getAdditionalNotes())
                .build();

        serviceRepository.save(service);

        return ApiResponse.builder()
                .success(true)
                .message("Service created successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> getAllServices() {
        LocalDate today = LocalDate.now();
        return serviceRepository.findAll().stream()
                .map(service -> mapToResponse(service, today))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long id) {
        com.example.demo.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        return mapToResponse(service, LocalDate.now());
    }

    @Transactional
    public ApiResponse updateService(Long id, ServiceRequest request) {
        com.example.demo.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        service.setServiceName(request.getServiceName());
        service.setCategory(request.getCategory());
        service.setDescription(request.getDescription());
        service.setEstimatedDuration(request.getEstimatedDuration());
        service.setBasePrice(request.getBasePrice());
        service.setRequiredSkills(request.getRequiredSkills());
        service.setPriority(request.getPriority());
        service.setMaxDailySlots(request.getMaxDailySlots());
        service.setServiceImage(request.getServiceImage());
        service.setAdditionalNotes(request.getAdditionalNotes());

        serviceRepository.save(service);

        return ApiResponse.builder()
                .success(true)
                .message("Service updated successfully")
                .build();
    }

    @Transactional
    public ApiResponse activateService(Long id) {
        com.example.demo.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        service.setIsActive(true);
        service.setAvailableSlots(service.getMaxDailySlots());
        serviceRepository.save(service);

        return ApiResponse.builder()
                .success(true)
                .message("Service activated successfully")
                .build();
    }

    @Transactional
    public ApiResponse deactivateService(Long id) {
        com.example.demo.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        service.setIsActive(false);
        serviceRepository.save(service);

        return ApiResponse.builder()
                .success(true)
                .message("Service deactivated successfully")
                .build();
    }

    @Transactional
    public ApiResponse deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found");
        }
        serviceRepository.deleteById(id);

        return ApiResponse.builder()
                .success(true)
                .message("Service deleted successfully")
                .build();
    }

    // Reset available slots daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void resetDailySlots() {
        List<com.example.demo.model.Service> services = serviceRepository.findAll();
        for (com.example.demo.model.Service service : services) {
            service.setAvailableSlots(service.getMaxDailySlots());
            // Auto-activate if was deactivated due to slots
            if (!service.getIsActive() && service.getAvailableSlots() > 0) {
                service.setIsActive(true);
            }
        }
        serviceRepository.saveAll(services);
    }

    // Auto-deactivate when slots are full
    @Transactional
    public void checkAndDeactivateIfFull(Long serviceId) {
        com.example.demo.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (service.getAvailableSlots() <= 0) {
            service.setIsActive(false);
            serviceRepository.save(service);
        }
    }

    private ServiceResponse mapToResponse(com.example.demo.model.Service service) {
        return mapToResponse(service, LocalDate.now());
    }

    private ServiceResponse mapToResponse(com.example.demo.model.Service service, LocalDate date) {
        // Count today's bookings for this service (excluding cancelled/rejected)
        Long todayBookingsCount = appointmentRepository.countAppointmentsByServiceAndDate(
                service.getServiceName(), date);
        
        int todayBookings = todayBookingsCount != null ? todayBookingsCount.intValue() : 0;
        int remainingSlots = service.getMaxDailySlots() - todayBookings;
        boolean availableToday = service.getIsActive() && remainingSlots > 0;
        
        return ServiceResponse.builder()
                .id(service.getId())
                .serviceName(service.getServiceName())
                .category(service.getCategory())
                .description(service.getDescription())
                .estimatedDuration(service.getEstimatedDuration())
                .basePrice(service.getBasePrice())
                .requiredSkills(service.getRequiredSkills())
                .priority(service.getPriority())
                .maxDailySlots(service.getMaxDailySlots())
                .availableSlots(service.getAvailableSlots())
                .serviceImage(service.getServiceImage())
                .isActive(service.getIsActive())
                .additionalNotes(service.getAdditionalNotes())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .todayBookings(todayBookings)
                .remainingSlots(Math.max(0, remainingSlots))
                .availableToday(availableToday)
                .build();
    }
}
