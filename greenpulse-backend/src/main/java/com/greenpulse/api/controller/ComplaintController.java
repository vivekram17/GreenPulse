package com.greenpulse.api.controller;

import com.greenpulse.api.dto.ComplaintResponse;
import com.greenpulse.api.dto.ComplaintSubmitRequest;
import com.greenpulse.api.dto.StatusUpdateRequest;
import com.greenpulse.api.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ComplaintResponse> submit(@Valid @RequestBody ComplaintSubmitRequest request) {
        return ResponseEntity.ok(complaintService.submit(request));
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> listAll() {
        return ResponseEntity.ok(complaintService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ComplaintResponse> updateStatus(@PathVariable Long id,
                                                           @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request.getStatus()));
    }
}
