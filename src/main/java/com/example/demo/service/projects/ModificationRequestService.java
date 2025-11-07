package com.example.demo.service.projects;

import com.example.demo.dto.projects.ModificationRequestDto;
import com.example.demo.model.projects.ModificationRequest;
import com.example.demo.enums.ModificationStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.projects.ModificationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModificationRequestService {

    private final ModificationRequestRepository modificationRequestRepository;

    public ModificationRequest createModificationRequest(ModificationRequestDto request) {
        ModificationRequest modificationRequest = new ModificationRequest();
        modificationRequest.setTitle(request.getTitle());
        modificationRequest.setDescription(request.getDescription());
        modificationRequest.setCustomerId(request.getCustomerId());
        modificationRequest.setVehicleId(request.getVehicleId());
        modificationRequest.setEstimatedCost(request.getEstimatedCost());
        modificationRequest.setStatus(ModificationStatus.PENDING);

        return modificationRequestRepository.save(modificationRequest);
    }

    public List<ModificationRequest> getModificationRequestsByCustomerId(Long customerId) {
        return modificationRequestRepository.findByCustomerId(customerId);
    }

    public List<ModificationRequest> getPendingModificationRequests() {
        return modificationRequestRepository.findAll()
                .stream()
                .filter(req -> req.getStatus() == ModificationStatus.PENDING)
                .collect(Collectors.toList());
    }

    public ModificationRequest updateModificationRequestStatus(Long requestId, ModificationStatus status) {
        ModificationRequest request = modificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Modification request not found"));

        request.setStatus(status);
        return modificationRequestRepository.save(request);
    }
}