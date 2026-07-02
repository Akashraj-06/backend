package com.fixly.repository;

import com.fixly.entity.JobAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobAssignmentRepository extends JpaRepository<JobAssignment, Long> {
    Optional<JobAssignment> findByServiceRequestId(Long serviceRequestId);
    List<JobAssignment> findByWorkerIdOrderByAcceptedAtDesc(Long workerId);
    List<JobAssignment> findByWorkerIdAndStatus(Long workerId, JobAssignment.Status status);
}
