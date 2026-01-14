package com.example.travel.repository;

import com.example.travel.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Reservation entity.
 * Supports optimistic locking through @Version field.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
