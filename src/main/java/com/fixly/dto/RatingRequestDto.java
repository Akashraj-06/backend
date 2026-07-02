package com.fixly.dto;

import lombok.Data;

@Data
public class RatingRequestDto {
    private Long serviceRequestId;
    private Integer rating;
}
