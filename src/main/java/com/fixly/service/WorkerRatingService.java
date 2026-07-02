package com.fixly.service;

import com.fixly.dto.RatingRequestDto;
import com.fixly.dto.RatingResponseDto;
import com.fixly.entity.ServiceRequest;
import com.fixly.entity.WorkerRating;
import com.fixly.entity.Worker;
import com.fixly.entity.User;
import com.fixly.repository.ServiceRequestRepository;
import com.fixly.repository.WorkerRatingRepository;
import com.fixly.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerRatingService {

    @Autowired
    private WorkerRatingRepository workerRatingRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Transactional
    public RatingResponseDto submitRating(Long customerId, RatingRequestDto requestDto) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(requestDto.getServiceRequestId())
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (!serviceRequest.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Customer not authorized to rate this service request");
        }

        if (serviceRequest.getStatus() != ServiceRequest.Status.COMPLETED) {
            throw new RuntimeException("Can only rate completed service requests");
        }

        if (workerRatingRepository.findByServiceRequestId(serviceRequest.getId()).isPresent()) {
            throw new RuntimeException("Service request already rated");
        }

        Worker worker = serviceRequest.getWorker();
        User customer = serviceRequest.getCustomer();

        WorkerRating workerRating = WorkerRating.builder()
                .serviceRequest(serviceRequest)
                .worker(worker)
                .customer(customer)
                .rating(requestDto.getRating())
                .build();

        WorkerRating savedRating = workerRatingRepository.save(workerRating);

        updateWorkerAverageRating(worker.getId());

        return RatingResponseDto.builder()
                .id(savedRating.getId())
                .serviceRequestId(savedRating.getServiceRequest().getId())
                .rating(savedRating.getRating())
                .build();
    }

    private void updateWorkerAverageRating(Long workerId) {
        List<WorkerRating> workerRatings = workerRatingRepository.findByWorkerId(workerId);
        
        if (!workerRatings.isEmpty()) {
            double average = workerRatings.stream()
                    .mapToInt(WorkerRating::getRating)
                    .average()
                    .orElse(0.0);
            
            Worker worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found"));
            worker.setRating(average);
            workerRepository.save(worker);
        }
    }
}
