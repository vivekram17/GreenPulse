package com.greenpulse.api.service;

import com.greenpulse.api.dto.ClassificationResult;
import com.greenpulse.api.dto.ComplaintResponse;
import com.greenpulse.api.dto.ComplaintSubmitRequest;
import com.greenpulse.api.model.Complaint;
import com.greenpulse.api.model.ComplaintStatus;
import com.greenpulse.api.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ComplaintService {

    private final ComplaintRepository repository;
    private final ClassificationService classificationService;
    private final DepartmentRoutingService routingService;

    public ComplaintService(ComplaintRepository repository,
                             ClassificationService classificationService,
                             DepartmentRoutingService routingService) {
        this.repository = repository;
        this.classificationService = classificationService;
        this.routingService = routingService;
    }

    public ComplaintResponse submit(ComplaintSubmitRequest request) {
        ClassificationResult result = classificationService.classify(request.getDescription());

        boolean aiFoundLocation = result.getLocation() != null
                && !"unspecified".equalsIgnoreCase(result.getLocation());
        boolean hintProvided = request.getLocationHint() != null && !request.getLocationHint().isBlank();
        String location = (!aiFoundLocation && hintProvided) ? request.getLocationHint() : result.getLocation();

        Complaint complaint = Complaint.builder()
                .description(request.getDescription())
                .photoUrl(request.getPhotoUrl())
                .category(result.getCategory())
                .urgency(result.getUrgency())
                .location(location)
                .status(ComplaintStatus.ROUTED)
                .department(routingService.routeFor(result.getCategory()))
                .aiReasoning(result.getReasoning())
                .build();

        Complaint saved = repository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    public List<ComplaintResponse> listAll() {
        return repository.findAll().stream().map(ComplaintResponse::fromEntity).toList();
    }

    public ComplaintResponse getById(Long id) {
        Complaint complaint = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Complaint " + id + " not found"));
        return ComplaintResponse.fromEntity(complaint);
    }

    public ComplaintResponse updateStatus(Long id, ComplaintStatus status) {
        Complaint complaint = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Complaint " + id + " not found"));
        complaint.setStatus(status);
        Complaint saved = repository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }
}
