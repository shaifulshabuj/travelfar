package com.example.travel.service;

import com.example.travel.dto.HotelResponse;
import com.example.travel.dto.HotelSearchRequest;
import com.example.travel.entity.Hotel;
import com.example.travel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for HotelSearchService.
 * Tests business logic and validation without Spring context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Hotel Search Service Tests")
class HotelSearchServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelSearchService hotelSearchService;

    private Hotel testHotel1;
    private Hotel testHotel2;

    @BeforeEach
    void setUp() {
        testHotel1 = Hotel.builder()
                .id(1L)
                .name("Grand Hotel Tokyo")
                .city("Tokyo")
                .pricePerNight(new BigDecimal("15000.00"))
                .rating(4.5)
                .description("Luxury hotel in downtown Tokyo")
                .totalRooms(100)
                .availableRooms(50)
                .build();

        testHotel2 = Hotel.builder()
                .id(2L)
                .name("Business Inn Tokyo")
                .city("Tokyo")
                .pricePerNight(new BigDecimal("8000.00"))
                .rating(4.0)
                .description("Affordable business hotel")
                .totalRooms(80)
                .availableRooms(30)
                .build();
    }

    @Test
    @DisplayName("Should successfully search hotels by city")
    void testSearchHotels_Success() {
        // Given
        HotelSearchRequest request = HotelSearchRequest.builder()
                .city("Tokyo")
                .checkIn(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(3))
                .guests(2)
                .page(0)
                .size(20)
                .build();

        List<Hotel> hotels = Arrays.asList(testHotel2, testHotel1); // Sorted by price
        Page<Hotel> hotelPage = new PageImpl<>(hotels);

        when(hotelRepository.findByCityIgnoreCaseAndAvailableRoomsGreaterThan(anyString(), any(Pageable.class)))
                .thenReturn(hotelPage);

        // When
        Page<HotelResponse> result = hotelSearchService.searchHotels(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Business Inn Tokyo");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Grand Hotel Tokyo");

        verify(hotelRepository).findByCityIgnoreCaseAndAvailableRoomsGreaterThan(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw exception when check-out date is before check-in date")
    void testSearchHotels_InvalidDateRange() {
        // Given
        HotelSearchRequest request = HotelSearchRequest.builder()
                .city("Tokyo")
                .checkIn(LocalDate.now().plusDays(5))
                .checkOut(LocalDate.now().plusDays(3)) // Before check-in
                .guests(2)
                .page(0)
                .size(20)
                .build();

        // When & Then
        assertThatThrownBy(() -> hotelSearchService.searchHotels(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-out date must be after check-in date");
    }

    @Test
    @DisplayName("Should throw exception when check-out date equals check-in date")
    void testSearchHotels_SameDateRange() {
        // Given
        LocalDate sameDate = LocalDate.now().plusDays(3);
        HotelSearchRequest request = HotelSearchRequest.builder()
                .city("Tokyo")
                .checkIn(sameDate)
                .checkOut(sameDate)
                .guests(2)
                .page(0)
                .size(20)
                .build();

        // When & Then
        assertThatThrownBy(() -> hotelSearchService.searchHotels(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-out date must be after check-in date");
    }

    @Test
    @DisplayName("Should return empty page when no hotels found")
    void testSearchHotels_NoResults() {
        // Given
        HotelSearchRequest request = HotelSearchRequest.builder()
                .city("Unknown City")
                .checkIn(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(3))
                .guests(2)
                .page(0)
                .size(20)
                .build();

        Page<Hotel> emptyPage = Page.empty();
        when(hotelRepository.findByCityIgnoreCaseAndAvailableRoomsGreaterThan(anyString(), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        Page<HotelResponse> result = hotelSearchService.searchHotels(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}
