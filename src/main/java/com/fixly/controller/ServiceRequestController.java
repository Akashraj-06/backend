package com.fixly.controller;

import com.fixly.dto.ServiceRequestDto;
import com.fixly.dto.ServiceRequestResponseDto;
import com.fixly.security.CustomUserDetails;
import com.fixly.service.ServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-request")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<ServiceRequestResponseDto> createRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ServiceRequestDto request
    ) {
        return ResponseEntity.ok(serviceRequestService.createRequest(userDetails.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequestResponseDto>> getMyRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(serviceRequestService.getCustomerRequests(userDetails.getId()));
    }
}
