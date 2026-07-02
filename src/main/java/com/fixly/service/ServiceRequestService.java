package com.fixly.service;

import com.fixly.dto.ServiceRequestDto;
import com.fixly.dto.ServiceRequestResponseDto;
import com.fixly.entity.ServiceCategory;
import com.fixly.entity.ServiceRequest;
import com.fixly.entity.User;
import com.fixly.entity.Worker;
import com.fixly.entity.WorkerRating;
import com.fixly.repository.ServiceCategoryRepository;
import com.fixly.repository.ServiceRequestRepository;
import com.fixly.repository.UserRepository;
import com.fixly.repository.WorkerRatingRepository;
import com.fixly.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final WorkerRatingRepository workerRatingRepository;

    @Transactional
    public ServiceRequestResponseDto createRequest(Long customerId, ServiceRequestDto dto) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Worker worker = workerRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        ServiceCategory category = worker.getServiceCategory();
        if (category == null) {
            throw new RuntimeException("Worker category not found");
        }

        ServiceRequest request = ServiceRequest.builder()
                .customer(customer)
                .worker(worker)
                .category(category)
                .description(dto.getDescription())
                .photoUrl(dto.getPhotoUrl())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .status(ServiceRequest.Status.PENDING)
                .build();

        ServiceRequest savedRequest = serviceRequestRepository.save(request);
        return mapToDto(savedRequest, false, null);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponseDto> getCustomerRequests(Long customerId) {
        List<ServiceRequest> requests = serviceRequestRepository.findCustomerRequests(customerId);

        // Batch-fetch all ratings for these service requests in a single query (avoids N+1)
        List<Long> requestIds = requests.stream()
                .map(ServiceRequest::getId)
                .collect(Collectors.toList());

        Map<Long, WorkerRating> ratingByRequestId = workerRatingRepository
                .findByServiceRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.toMap(
                        r -> r.getServiceRequest().getId(),
                        r -> r
                ));

        return requests.stream()
                .map(req -> {
                    WorkerRating rating = ratingByRequestId.get(req.getId());
                    boolean alreadyRated = rating != null;
                    Integer userRating = alreadyRated ? rating.getRating() : null;
                    return mapToDto(req, alreadyRated, userRating);
                })
                .collect(Collectors.toList());
    }

    public ServiceRequest getRequest(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    private ServiceRequestResponseDto mapToDto(ServiceRequest entity, boolean alreadyRated, Integer userRating) {
        return ServiceRequestResponseDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .photoUrl(entity.getPhotoUrl())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .alreadyRated(alreadyRated)
                .userRating(userRating)
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getId() : null)
                .customerName(entity.getCustomer() != null ? entity.getCustomer().getName() : null)
                .workerId(entity.getWorker() != null ? entity.getWorker().getId() : null)
                .workerName(entity.getWorker() != null && entity.getWorker().getUser() != null
                        ? entity.getWorker().getUser().getName() : null)
                .build();
    }
}
