package com.example.travel.controller;

import com.example.travel.dto.HotelResponse;
import com.example.travel.dto.HotelSearchRequest;
import com.example.travel.service.HotelSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for hotel search operations.
 * No business logic here - delegates to service layer.
 */
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Hotel Search", description = "APIs for searching hotels")
public class HotelSearchController {

    private final HotelSearchService hotelSearchService;

    /**
     * Search hotels by city and date range.
     * Results are cached in Redis for performance.
     *
     * @param city     City name (required)
     * @param checkIn  Check-in date (required)
     * @param checkOut Check-out date (required)
     * @param guests   Number of guests (required, min=1)
     * @param page     Page number (default=0)
     * @param size     Page size (default=20)
     * @return Paginated list of hotels
     */
    @Operation(
            summary = "Search hotels",
            description = "Search for available hotels by city and date range. Results are paginated and cached."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels found successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<HotelResponse>> searchHotels(
            @Parameter(description = "City name", required = true)
            @RequestParam String city,

            @Parameter(description = "Check-in date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,

            @Parameter(description = "Check-out date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,

            @Parameter(description = "Number of guests", required = true)
            @RequestParam @Min(1) Integer guests,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        log.info("Received hotel search request: city={}, checkIn={}, checkOut={}, guests={}, page={}, size={}",
                city, checkIn, checkOut, guests, page, size);

        HotelSearchRequest request = HotelSearchRequest.builder()
                .city(city)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(guests)
                .page(page)
                .size(size)
                .build();

        Page<HotelResponse> results = hotelSearchService.searchHotels(request);

        log.info("Returning {} hotels for city: {}", results.getTotalElements(), city);

        return ResponseEntity.ok(results);
    }
}
