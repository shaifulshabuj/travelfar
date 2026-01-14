package com.example.travel.service;

import com.example.travel.dto.ReservationRequest;
import com.example.travel.dto.ReservationResponse;
import com.example.travel.entity.Hotel;
import com.example.travel.entity.Reservation;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.HotelRepository;
import com.example.travel.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for reservation operations.
 * Handles transaction boundaries and business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;

    /**
     * Create a new reservation with proper validation and transaction management.
     * Uses optimistic locking to prevent double-booking.
     *
     * @param request reservation details
     * @return created reservation
     * @throws ResourceNotFoundException if hotel not found
     * @throws IllegalArgumentException  if validation fails
     */
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creating reservation for hotel: {}, guest: {}, checkIn: {}, checkOut: {}",
                request.getHotelId(), request.getGuestName(), request.getCheckIn(), request.getCheckOut());

        // Validate date range
        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Verify hotel exists
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + request.getHotelId()
                ));

        // Check room availability
        if (hotel.getAvailableRooms() == null || hotel.getAvailableRooms() < 1) {
            throw new IllegalArgumentException("No rooms available at this hotel");
        }

        log.debug("Hotel found: {}, available rooms: {}", hotel.getName(), hotel.getAvailableRooms());

        // Create reservation entity
        Reservation reservation = Reservation.builder()
                .hotelId(request.getHotelId())
                .guestName(request.getGuestName())
                .guestEmail(request.getGuestEmail())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guests(request.getGuests())
                .build();

        // Save reservation (optimistic locking via @Version)
        Reservation savedReservation = reservationRepository.save(reservation);

        // Update hotel availability
        hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
        hotelRepository.save(hotel);

        log.info("Reservation created successfully with id: {}", savedReservation.getId());

        // Map to response DTO
        return mapToResponse(savedReservation);
    }

    /**
     * Map Reservation entity to ReservationResponse DTO.
     */
    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .hotelId(reservation.getHotelId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkIn(reservation.getCheckIn())
                .checkOut(reservation.getCheckOut())
                .guests(reservation.getGuests())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
