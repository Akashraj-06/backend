package com.fixly.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * WorkerRating entity – stores a rating given by a customer for a completed ServiceRequest.
 * One rating per ServiceRequest (unique constraint on serviceRequest).
 */
@Entity
@Table(name = "worker_ratings",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"service_request_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false)
    private Integer rating; // 1‑5 stars

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
