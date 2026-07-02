package com.fixly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerJobDto {
    private Long id; // ServiceRequest ID
    private Long jobAssignmentId; // JobAssignment ID (null if PENDING)
    private String customerName;
    private String categoryName;
    private String description;
    private String address;
    private LocalDateTime requestedDate;
    private String status; // PENDING, ASSIGNED, COMPLETED, etc.
}
