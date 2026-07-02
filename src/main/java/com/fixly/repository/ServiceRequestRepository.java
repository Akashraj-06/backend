package com.fixly.repository;

import com.fixly.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    
    @Query("""
    SELECT sr
    FROM ServiceRequest sr
    JOIN FETCH sr.customer
    JOIN FETCH sr.category
    LEFT JOIN FETCH sr.worker w
    LEFT JOIN FETCH w.user
    WHERE sr.customer.id = :customerId
    ORDER BY sr.createdAt DESC
    """)
    List<ServiceRequest> findCustomerRequests(@Param("customerId") Long customerId);

    @Query("""
    SELECT sr
    FROM ServiceRequest sr
    JOIN FETCH sr.customer
    JOIN FETCH sr.category
    JOIN FETCH sr.worker w
    JOIN FETCH w.user
    WHERE w.id = :workerId AND sr.status = 'PENDING'
    ORDER BY sr.createdAt DESC
    """)
    List<ServiceRequest> findPendingRequestsByWorkerId(@Param("workerId") Long workerId);
    
    List<ServiceRequest> findByStatusOrderByCreatedAtDesc(ServiceRequest.Status status);
    List<ServiceRequest> findByCategoryIdAndStatusOrderByCreatedAtDesc(Long categoryId, ServiceRequest.Status status);
}
