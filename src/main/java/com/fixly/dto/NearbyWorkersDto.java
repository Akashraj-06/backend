package com.fixly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyWorkersDto {
    private Long workerId;
    private String name;
    private Double rating;
    private Integer totalJobs;
    private Double distance; // in km
    private String categoryName;
}
