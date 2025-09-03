package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.BookerState;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingRequest request);

    BookingDto approvedBooking(Long userId, Long bookingId, String approve);

    BookingDto findBookingById(Long bookingId, Long userId);

    Collection<BookingDto> findBookingByUserAndState(Long userId, BookerState state);

    Collection<BookingDto> findBookingByOwner(Long ownerId, BookerState state);
}
