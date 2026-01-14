package com.example.travel.controller;

import com.example.travel.dto.ReservationRequest;
import com.example.travel.dto.ReservationResponse;
import com.example.travel.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for reservation operations.
 * Handles reservation creation with proper validation.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservations", description = "APIs for managing hotel reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Create a new hotel reservation.
     * Validates input and ensures hotel availability.
     *
     * @param request Reservation details
     * @return Created reservation with ID
     */
    @Operation(
            summary = "Create a reservation",
            description = "Create a new hotel reservation with guest details and dates"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request
    ) {
        log.info("Received reservation request: hotelId={}, guestName={}, checkIn={}, checkOut={}",
                request.getHotelId(), request.getGuestName(), request.getCheckIn(), request.getCheckOut());

        ReservationResponse response = reservationService.createReservation(request);

        log.info("Reservation created successfully with id: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
