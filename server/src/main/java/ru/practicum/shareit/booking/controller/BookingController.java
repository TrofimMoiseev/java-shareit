package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.BookerState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingRequest request,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestParam String approved,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> findBookingsByUserAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam BookerState state) {
        return bookingService.findBookingByUserAndState(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @RequestParam BookerState state) {
        return bookingService.findBookingByOwner(ownerId, state);
    }
}
