package com.fixly.repository;

import com.fixly.entity.WorkerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRatingRepository extends JpaRepository<WorkerRating, Long> {
    Optional<WorkerRating> findByServiceRequestId(Long serviceRequestId);
    List<WorkerRating> findByWorkerId(Long workerId);
    List<WorkerRating> findByServiceRequestIdIn(Collection<Long> serviceRequestIds);
}
