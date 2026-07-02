package com.fixly.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponseDto {
    private Long id;
    private Long serviceRequestId;
    private Integer rating;
}
