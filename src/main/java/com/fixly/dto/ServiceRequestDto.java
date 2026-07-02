package com.fixly.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ServiceRequestDto {
    @NotNull
    private Long workerId;
    
    private String description;
    private String photoUrl;
    
    @NotNull
    private Double latitude;
    
    @NotNull
    private Double longitude;
    
    private String address;
}
