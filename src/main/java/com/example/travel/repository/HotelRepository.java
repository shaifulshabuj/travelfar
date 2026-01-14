package com.example.travel.repository;

import com.example.travel.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Hotel entity.
 * Uses Spring Data JPA for database operations.
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Find hotels by city with pagination.
     * Query is optimized for high-traffic read operations.
     *
     * @param city     the city to search in
     * @param pageable pagination information
     * @return paginated list of hotels
     */
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.city) = LOWER(:city) AND h.availableRooms > 0")
    Page<Hotel> findByCityIgnoreCaseAndAvailableRoomsGreaterThan(
            @Param("city") String city,
            Pageable pageable
    );
}
