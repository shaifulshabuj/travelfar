package com.example.travel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Request DTO for hotel search.
 * Implements Serializable for Redis caching.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSearchRequest implements Serializable {

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    private Integer guests;

    @Min(value = 0, message = "Page number must be non-negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    private int size = 20;
}
