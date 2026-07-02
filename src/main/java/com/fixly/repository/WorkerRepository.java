package com.fixly.repository;

import com.fixly.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByUserId(Long userId);

    List<Worker> findByIsAvailableTrue();

    List<Worker> findByServiceCategory_IdAndIsAvailableTrue(Long serviceCategoryId);
    /**
     * Find nearby available workers using the Haversine formula (km radius).
     */
    @Query(value = """
        SELECT w.* FROM workers w
        WHERE w.is_available = true
        AND (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(w.latitude)) *
                cos(radians(w.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(w.latitude))
            )
        ) <= :radius
        ORDER BY (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(w.latitude)) *
                cos(radians(w.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(w.latitude))
            )
        ) ASC
        LIMIT 20
        """, nativeQuery = true)
    List<Worker> findNearbyWorkers(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius
    );
}
