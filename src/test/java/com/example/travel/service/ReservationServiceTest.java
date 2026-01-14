package com.example.travel.service;

import com.example.travel.dto.ReservationRequest;
import com.example.travel.dto.ReservationResponse;
import com.example.travel.entity.Hotel;
import com.example.travel.entity.Reservation;
import com.example.travel.exception.ResourceNotFoundException;
import com.example.travel.repository.HotelRepository;
import com.example.travel.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationService.
 * Tests reservation creation logic and validation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Reservation Service Tests")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Hotel testHotel;
    private ReservationRequest validRequest;

    @BeforeEach
    void setUp() {
        testHotel = Hotel.builder()
                .id(1L)
                .name("Grand Hotel Tokyo")
                .city("Tokyo")
                .pricePerNight(new BigDecimal("15000.00"))
                .rating(4.5)
                .totalRooms(100)
                .availableRooms(50)
                .build();

        validRequest = ReservationRequest.builder()
                .hotelId(1L)
                .guestName("John Doe")
                .guestEmail("john.doe@example.com")
                .checkIn(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(3))
                .guests(2)
                .build();
    }

    @Test
    @DisplayName("Should successfully create reservation")
    void testCreateReservation_Success() {
        // Given
        Reservation savedReservation = Reservation.builder()
                .id(1L)
                .hotelId(validRequest.getHotelId())
                .guestName(validRequest.getGuestName())
                .guestEmail(validRequest.getGuestEmail())
                .checkIn(validRequest.getCheckIn())
                .checkOut(validRequest.getCheckOut())
                .guests(validRequest.getGuests())
                .build();

        when(hotelRepository.findById(anyLong())).thenReturn(Optional.of(testHotel));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // When
        ReservationResponse response = reservationService.createReservation(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getGuestName()).isEqualTo("John Doe");
        assertThat(response.getHotelId()).isEqualTo(1L);

        verify(hotelRepository).findById(1L);
        verify(reservationRepository).save(any(Reservation.class));
        verify(hotelRepository).save(testHotel);
        assertThat(testHotel.getAvailableRooms()).isEqualTo(49); // Decreased by 1
    }

    @Test
    @DisplayName("Should throw exception when hotel not found")
    void testCreateReservation_HotelNotFound() {
        // Given
        when(hotelRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hotel not found with id: 1");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when no rooms available")
    void testCreateReservation_NoRoomsAvailable() {
        // Given
        testHotel.setAvailableRooms(0);
        when(hotelRepository.findById(anyLong())).thenReturn(Optional.of(testHotel));

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No rooms available at this hotel");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when check-out date is before check-in date")
    void testCreateReservation_InvalidDateRange() {
        // Given
        validRequest.setCheckIn(LocalDate.now().plusDays(5));
        validRequest.setCheckOut(LocalDate.now().plusDays(3));

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-out date must be after check-in date");

        verify(hotelRepository, never()).findById(anyLong());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
