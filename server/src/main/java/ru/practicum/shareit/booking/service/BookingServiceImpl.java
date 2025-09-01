package ru.practicum.shareit.booking.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookerState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingRequest request) {
        if (request.getEnd().isBefore(request.getStart()) || request.getEnd().equals(request.getStart())) {
            throw new RuntimeException("Указаны неверные даты бронирования");
        }
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена"));
        if (!item.getAvailable()) {
            throw new RuntimeException("Данная вещь уже забронирована");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approvedBooking(Long userId, Long bookingId, String approved) {
        log.debug("Выполняем подтверждение брони");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование по данному ID не найдено"));
        if (booking.getItem().getOwnerId().equals(userId)) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                log.debug("Бронирование уже было обработано");
                throw new IllegalStateException("Бронирование уже обработано");
            }
            if (approved.equalsIgnoreCase("true")) {
                log.debug("Подтверждаем бронь");
                booking.setStatus(Status.APPROVED);
            } else if (approved.equalsIgnoreCase("false")) {
                log.debug("Отклоняем бронь");
                booking.setStatus(Status.REJECTED);
            } else {
                booking.setStatus(Status.CANCELED);
            }
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new RuntimeException("Подтвердить бронирование может только владелец вещи");
        }
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        if (userRepository.existsById(userId)) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NotFoundException("Бронирование по данному ID не найдено"));
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Пользователь с данным ID не найден");
        }
    }

    @Override
    public Collection<BookingDto> findBookingByUserAndState(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с данным ID не найден");
        }
        try {
            Collection<Booking> bookings;
            Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
            BookerState bookerState = BookerState.valueOf(state.toUpperCase());
            LocalDateTime now = LocalDateTime.now();
            bookings = switch (bookerState) {
                case ALL -> bookingRepository.findByBookerId(userId, newestFirst);
                case CURRENT ->
                        bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, newestFirst);
                case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, now, newestFirst);
                case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, now, newestFirst);
                case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, newestFirst);
                case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, newestFirst);
            };
            return BookingMapper.toBookingDtoList(bookings);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Неверный параметр state: " + state);
        }
    }

    @Override
    public Collection<BookingDto> findBookingByOwner(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с данным ID не найден");
        }
        try {
            Collection<Booking> bookings;
            Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
            BookerState bookerState = BookerState.valueOf(state.toUpperCase());
            LocalDateTime now = LocalDateTime.now();
            bookings = switch (bookerState) {
                case ALL -> bookingRepository.findByItem_OwnerId(ownerId, newestFirst);
                case CURRENT -> bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, newestFirst);
                case PAST -> bookingRepository.findByItem_OwnerIdAndEndIsBefore(ownerId, now, newestFirst);
                case FUTURE -> bookingRepository.findByItem_OwnerIdAndStartAfter(ownerId, now, newestFirst);
                case WAITING -> bookingRepository.findByItem_OwnerIdAndStatus(ownerId, Status.WAITING, newestFirst);
                case REJECTED -> bookingRepository.findByItem_OwnerIdAndStatus(ownerId, Status.REJECTED, newestFirst);
            };
            return BookingMapper.toBookingDtoList(bookings);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Неверный параметр state: " + state);
        }
    }
}
