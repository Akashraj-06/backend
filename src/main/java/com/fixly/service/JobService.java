package com.fixly.service;

import com.fixly.dto.WorkerJobDto;
import com.fixly.entity.JobAssignment;
import com.fixly.entity.ServiceRequest;
import com.fixly.entity.Worker;
import com.fixly.entity.WorkerRating;
import com.fixly.repository.JobAssignmentRepository;
import com.fixly.repository.ServiceRequestRepository;
import com.fixly.repository.WorkerRatingRepository;
import com.fixly.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobAssignmentRepository jobAssignmentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final WorkerRepository workerRepository;
    private final WorkerRatingRepository workerRatingRepository;

    @Transactional
    public JobAssignment acceptJob(Long workerUserId, Long serviceRequestId) {
        Worker worker = workerRepository.findByUserId(workerUserId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        
        ServiceRequest request = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));
                
        // Concurrency and Security Checks
        if (request.getWorker() == null || !request.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("This request is not assigned to you");
        }

        if (jobAssignmentRepository.findByServiceRequestId(serviceRequestId).isPresent()) {
            throw new RuntimeException("Job already accepted");
        }

        if (request.getStatus() != ServiceRequest.Status.PENDING) {
            throw new RuntimeException("Request is no longer pending");
        }

        // Update Request status
        request.setStatus(ServiceRequest.Status.ACCEPTED);
        serviceRequestRepository.save(request);

        // Update Worker availability
        worker.setIsAvailable(false);
        workerRepository.save(worker);

        // Create Job Assignment
        JobAssignment assignment = JobAssignment.builder()
                .serviceRequest(request)
                .worker(worker)
                .status(JobAssignment.Status.ASSIGNED)
                .build();
                
        return jobAssignmentRepository.save(assignment);
    }

    public JobAssignment getJobStatus(Long id) {
        return jobAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job assignment not found"));
    }

    @Transactional
    public JobAssignment completeJob(Long id) {
        JobAssignment job = jobAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job assignment not found"));
                
        job.setStatus(JobAssignment.Status.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        
        ServiceRequest req = job.getServiceRequest();
        req.setStatus(ServiceRequest.Status.COMPLETED);
        serviceRequestRepository.save(req);
        
        Worker worker = job.getWorker();
        worker.setIsAvailable(true);
        worker.setTotalJobs(worker.getTotalJobs() + 1);
        workerRepository.save(worker);
        
        return jobAssignmentRepository.save(job);
    }

    @Transactional(readOnly = true)
    public List<WorkerJobDto> getWorkerJobs(Long userId) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // 1. Get PENDING requests targeted to this worker
        List<ServiceRequest> pendingRequests = serviceRequestRepository.findPendingRequestsByWorkerId(worker.getId());
        List<WorkerJobDto> jobs = pendingRequests.stream().map(req -> 
            WorkerJobDto.builder()
                .id(req.getId())
                .jobAssignmentId(null)
                .customerName(req.getCustomer() != null ? req.getCustomer().getName() : "Unknown")
                .customerPhone(req.getCustomer() != null ? req.getCustomer().getPhone() : null)
                .categoryName(req.getCategory() != null ? req.getCategory().getName() : "Unknown")
                .description(req.getDescription())
                .address(req.getAddress())
                .requestedDate(req.getCreatedAt())
                .status("PENDING")
                .photoUrl(req.getPhotoUrl())
                .build()
        ).collect(Collectors.toList());

        // 2. Get accepted JobAssignments for this worker
        List<JobAssignment> assignments = jobAssignmentRepository.findByWorkerIdOrderByAcceptedAtDesc(worker.getId());
        List<WorkerJobDto> assignedJobs = assignments.stream().map(ja -> {
            ServiceRequest req = ja.getServiceRequest();
            return WorkerJobDto.builder()
                .id(req != null ? req.getId() : null)
                .jobAssignmentId(ja.getId())
                .customerName(req != null && req.getCustomer() != null ? req.getCustomer().getName() : "Unknown")
                .customerPhone(req != null && req.getCustomer() != null ? req.getCustomer().getPhone() : null)
                .categoryName(req != null && req.getCategory() != null ? req.getCategory().getName() : "Unknown")
                .description(req != null ? req.getDescription() : "")
                .address(req != null ? req.getAddress() : "")
                .requestedDate(ja.getAcceptedAt())
                .status(ja.getStatus().toString())
                .photoUrl(req != null ? req.getPhotoUrl() : null)
                .build();
        }).collect(Collectors.toList());

        jobs.addAll(assignedJobs);

        // Sort by requestedDate desc
        jobs.sort((j1, j2) -> {
            if (j1.getRequestedDate() == null && j2.getRequestedDate() == null) return 0;
            if (j1.getRequestedDate() == null) return 1;
            if (j2.getRequestedDate() == null) return -1;
            return j2.getRequestedDate().compareTo(j1.getRequestedDate());
        });

        // Batch-load customer ratings for all jobs in a single query
        Set<Long> serviceRequestIds = jobs.stream()
                .map(WorkerJobDto::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (!serviceRequestIds.isEmpty()) {
            Map<Long, Integer> ratingByServiceRequestId = workerRatingRepository
                    .findByServiceRequestIdIn(serviceRequestIds)
                    .stream()
                    .collect(Collectors.toMap(
                            r -> r.getServiceRequest().getId(),
                            WorkerRating::getRating
                    ));
            jobs.forEach(job -> {
                if (job.getId() != null) {
                    job.setCustomerRating(ratingByServiceRequestId.get(job.getId()));
                }
            });
        }

        return jobs;
    }
}
