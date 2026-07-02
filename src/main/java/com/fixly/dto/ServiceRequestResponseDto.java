package com.fixly.dto;

import com.fixly.entity.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestResponseDto {
    private Long id;
    private String description;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
    private String address;
    private ServiceRequest.Status status;
    private LocalDateTime createdAt;
    
    // Rating Details
    private boolean alreadyRated;
    private Integer userRating;
    
    // Category Details
    private Long categoryId;
    private String categoryName;
    
    // Customer Details
    private Long customerId;
    private String customerName;
    
    // Worker Details
    private Long workerId;
    private String workerName;
}
