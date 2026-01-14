package com.example.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reservation entity with optimistic locking for concurrency control.
 * Prevents double-booking scenarios in high-traffic environments.
 */
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_hotel_id", columnList = "hotel_id"),
    @Index(name = "idx_check_in_out", columnList = "check_in, check_out")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "guest_name", nullable = false, length = 200)
    private String guestName;

    @Column(name = "guest_email", nullable = false, length = 200)
    private String guestEmail;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private Integer guests;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Optimistic locking version field.
     * Prevents lost updates in concurrent reservation scenarios.
     */
    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
