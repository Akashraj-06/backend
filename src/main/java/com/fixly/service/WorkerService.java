package com.fixly.service;

import com.fixly.dto.NearbyWorkersDto;
import com.fixly.entity.Worker;
import com.fixly.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;

     @Transactional(readOnly = true)
    public List<NearbyWorkersDto> findNearbyWorkers(
            Double lat,
            Double lng,
            Double radiusKm)  {
        List<Worker> workers = workerRepository.findNearbyWorkers(lat, lng, radiusKm);
        
        return workers.stream().map(w -> {
            // Calculate actual distance for the DTO (simplified Haversine here or return directly from query)
            double dist = calculateDistance(lat, lng, w.getLatitude(), w.getLongitude());
            
            return NearbyWorkersDto.builder()
                    .workerId(w.getId())
                    .name(w.getUser().getName())
                    .rating(w.getRating())
                    .totalJobs(w.getTotalJobs())
                    .distance(Math.round(dist * 10.0) / 10.0) // Round to 1 decimal
                    .categoryName(w.getServiceCategory().getName())
                    .build();
        }).collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + 
                          Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344; // in Kilometers
            return dist;
        }
    }
}
