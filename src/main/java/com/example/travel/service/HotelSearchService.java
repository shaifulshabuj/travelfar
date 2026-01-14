package com.example.travel.service;

import com.example.travel.dto.HotelResponse;
import com.example.travel.dto.HotelSearchRequest;
import com.example.travel.entity.Hotel;
import com.example.travel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for hotel search operations.
 * Implements caching strategy for read-heavy traffic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HotelSearchService {

    private final HotelRepository hotelRepository;

    /**
     * Search hotels by city with Redis caching.
     * Cache key includes all search parameters for accurate cache hits.
     * TTL is configured to 5 minutes in application.yml.
     *
     * @param request search parameters
     * @return paginated hotel results
     */
    @Cacheable(
            value = "hotelSearch",
            key = "#request.city + '_' + #request.checkIn + '_' + #request.checkOut + '_' + #request.guests + '_' + #request.page + '_' + #request.size"
    )
    public Page<HotelResponse> searchHotels(HotelSearchRequest request) {
        log.info("Searching hotels in city: {}, checkIn: {}, checkOut: {}, guests: {}, page: {}, size: {}",
                request.getCity(), request.getCheckIn(), request.getCheckOut(),
                request.getGuests(), request.getPage(), request.getSize());

        // Validate date range
        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Create pageable with sorting by price ascending
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.ASC, "pricePerNight")
        );

        // Execute repository query
        Page<Hotel> hotelPage = hotelRepository.findByCityIgnoreCaseAndAvailableRoomsGreaterThan(
                request.getCity(),
                pageable
        );

        log.debug("Found {} hotels in city: {}", hotelPage.getTotalElements(), request.getCity());

        // Map entities to DTOs
        return hotelPage.map(this::mapToResponse);
    }

    /**
     * Map Hotel entity to HotelResponse DTO.
     * Never expose entity directly to API layer.
     */
    private HotelResponse mapToResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .pricePerNight(hotel.getPricePerNight())
                .rating(hotel.getRating())
                .description(hotel.getDescription())
                .availableRooms(hotel.getAvailableRooms())
                .build();
    }
}
