package com.fixly.controller;

import com.fixly.dto.WorkerJobDto;
import com.fixly.entity.JobAssignment;
import com.fixly.entity.User;
import com.fixly.security.CustomUserDetails;
import com.fixly.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/accept/{serviceRequestId}")
    public ResponseEntity<JobAssignment> acceptJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long serviceRequestId
    ) {
        if (userDetails.getUser().getRole() != User.Role.WORKER) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(jobService.acceptJob(userDetails.getId(), serviceRequestId));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobAssignment> getJobStatus(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobStatus(jobId));
    }

    @PostMapping("/complete/{jobId}")
    public ResponseEntity<JobAssignment> completeJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long jobId
    ) {
        if (userDetails.getUser().getRole() != User.Role.WORKER) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(jobService.completeJob(jobId));
    }

    @GetMapping("/worker")
    public ResponseEntity<List<WorkerJobDto>> getWorkerJobs(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails.getUser().getRole() != User.Role.WORKER) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(jobService.getWorkerJobs(userDetails.getId()));
    }
}
