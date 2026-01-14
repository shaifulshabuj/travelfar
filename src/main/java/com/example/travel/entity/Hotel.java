package com.example.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Hotel entity representing a hotel in the system.
 * Optimized for read-heavy operations with proper indexing.
 */
@Entity
@Table(name = "hotels", indexes = {
    @Index(name = "idx_city", columnList = "city"),
    @Index(name = "idx_price", columnList = "price_per_night")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private Double rating;

    @Column(length = 1000)
    private String description;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "available_rooms")
    private Integer availableRooms;
}
