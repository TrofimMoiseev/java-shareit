package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private BookingServiceImpl bookingService;

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private static MockedStatic<BookingMapper> bookingMapperMock;

    private final Long userId = 1L;
    private final Long bookingId = 10L;
    private final Long itemId = 100L;

    private Booking booking;
    private BookingDto bookingDto;
    private User user;
    private Item item;

    @BeforeAll
    static void beforeAll() {
        bookingMapperMock = Mockito.mockStatic(BookingMapper.class);
    }

    @AfterAll
    static void afterAll() {
        bookingMapperMock.close();
    }

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(itemId);
        item.setName("Item");
        item.setAvailable(true);
        item.setOwnerId(2L);

        booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
    }

    @Test
    void createBooking_shouldReturnBookingDto() {
        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingMapperMock.when(() -> BookingMapper.toBookingDto(any())).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(userId, request);

        assertEquals(bookingDto.getId(), result.getId());
    }

    @Test
    void createBooking_shouldThrow_whenItemUnavailable() {
        item.setAvailable(false);
        BookingRequest request = new BookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(userId, request));

        assertEquals("Данная вещь уже забронирована", ex.getMessage());
    }

    @Test
    void approvedBooking_shouldApproveBooking() {
        item.setOwnerId(userId); // текущий пользователь — владелец
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingMapperMock.when(() -> BookingMapper.toBookingDto(any())).thenReturn(bookingDto);

        BookingDto result = bookingService.approvedBooking(userId, bookingId, "true");

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void approvedBooking_shouldRejectBooking() {
        item.setOwnerId(userId);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingMapperMock.when(() -> BookingMapper.toBookingDto(any())).thenReturn(bookingDto);

        BookingDto result = bookingService.approvedBooking(userId, bookingId, "false");

        assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    void approvedBooking_shouldThrowIfNotOwner() {
        item.setOwnerId(999L);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.approvedBooking(userId, bookingId, "true"));

        assertTrue(ex.getMessage().contains("владелец"));
    }

    @Test
    void findBookingById_shouldReturnBooking() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        bookingMapperMock.when(() -> BookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.findBookingById(bookingId, userId);

        assertEquals(bookingDto.getId(), result.getId());
    }

    @Test
    void findBookingById_shouldThrowIfUserNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(bookingId, userId));

        assertEquals("Пользователь с данным ID не найден", ex.getMessage());
    }

    @Test
    void findBookingByUserAndState_all_shouldReturnList() {
        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerId(eq(userId), any())).thenReturn(bookings);
        bookingMapperMock.when(() -> BookingMapper.toBookingDtoList(bookings)).thenReturn(List.of(bookingDto));

        Collection<BookingDto> result = bookingService.findBookingByUserAndState(userId, "ALL");

        assertEquals(1, result.size());
    }

    @Test
    void findBookingByOwner_waiting_shouldReturnList() {
        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerIdAndStatus(eq(userId), eq(Status.WAITING), any()))
                .thenReturn(bookings);
        bookingMapperMock.when(() -> BookingMapper.toBookingDtoList(bookings)).thenReturn(List.of(bookingDto));

        Collection<BookingDto> result = bookingService.findBookingByOwner(userId, "WAITING");

        assertEquals(1, result.size());
    }

    @Test
    void findBookingByOwner_shouldThrowForInvalidState() {
        when(userRepository.existsById(userId)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.findBookingByOwner(userId, "INVALID"));

        assertTrue(ex.getMessage().contains("Неверный параметр state"));
    }
}
