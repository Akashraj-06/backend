package com.fixly.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * JobAssignment entity — links a ServiceRequest to an accepting Worker
 */
@Entity
@Table(name = "job_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    @JsonIgnore
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    @JsonIgnore
    private Worker worker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ASSIGNED;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_arrival_minutes")
    private Integer estimatedArrivalMinutes;

    @PrePersist
    protected void onCreate() {
        acceptedAt = LocalDateTime.now();
    }

    public enum Status {
        ASSIGNED, EN_ROUTE, ARRIVED, COMPLETED, CANCELLED
    }
}
