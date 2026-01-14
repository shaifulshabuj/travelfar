package com.example.travel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Response DTO for hotel information.
 * Never exposes entity directly to API consumers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse implements Serializable {

    private Long id;
    private String name;
    private String city;
    private BigDecimal pricePerNight;
    private Double rating;
    private String description;
    private Integer availableRooms;
}
