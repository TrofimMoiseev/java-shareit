package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private final Long userId = 1L;
    private final Long itemId = 2L;
    private final User user = new User(userId, "Test", "test@mail.com");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllByOwnerId_shouldReturnItems() {
        Item item = new Item(itemId, "Item 1", "Desc", true, userId, null);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.findAllByOwnerId(userId);

        assertEquals(1, result.size());
        assertEquals(itemId, result.get(0).getId());
    }

    @Test
    void findAllByOwnerId_shouldThrowIfUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.findAllByOwnerId(userId));
    }

    @Test
    void findById_shouldReturnItemDto() {
        Item item = new Item(itemId, "Item", "Desc", true, userId, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.findById(itemId);
        assertEquals(itemId, result.getId());
    }

    @Test
    void findById_shouldThrowIfNotExists() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(itemId));
    }

    @Test
    void getSearch_shouldReturnEmptyIfTextBlank() {
        assertTrue(itemService.getSearch(" ").isEmpty());
    }

    @Test
    void getSearch_shouldReturnMatchedItems() {
        Item item = new Item(itemId, "Drill", "desc", true, userId, null);
        when(itemRepository.search("drill")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getSearch("drill");

        assertEquals(1, result.size());
        assertEquals(itemId, result.get(0).getId());
    }

    @Test
    void save_shouldSaveItem() {
        Item item = new Item(null, "Name", "desc", true, null, null);
        Item savedItem = new Item(itemId, "Name", "desc", true, userId, null);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.save(userId, item);

        assertEquals(itemId, result.getId());
        verify(itemRepository).save(item);
    }

    @Test
    void save_shouldThrowIfUserNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);
        Item item = new Item();
        assertThrows(NotFoundException.class, () -> itemService.save(userId, item));
    }

    @Test
    void update_shouldUpdateItemFields() {
        Item existing = new Item(itemId, "Old", "Old desc", false, userId, null);
        ItemDto updates = new ItemDto();
        updates.setName("New");
        updates.setDescription("New desc");
        updates.setAvailable(true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any())).thenReturn(existing);

        ItemDto result = itemService.update(userId, itemId, updates);

        assertEquals("New", result.getName());
        assertEquals("New desc", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void update_shouldThrowIfNotOwner() {
        Item item = new Item(itemId, "Old", "Desc", true, 999L, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.update(userId, itemId, new ItemDto()));
    }

    @Test
    void addNewComment_shouldSaveIfBookingExists() {
        CommentRequest request = new CommentRequest();
        request.setText("Nice!");

        Item item = new Item(itemId, "Test", "desc", true, userId, null);
        Comment comment = new Comment(1L, "Nice!", item, user, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsPastBookingExcludingRejected(eq(userId), eq(itemId), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.addNewComment(itemId, userId, request);

        assertEquals("Nice!", result.getText());
    }

    @Test
    void addNewComment_shouldThrowIfNoBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.existsPastBookingExcludingRejected(eq(userId), eq(itemId), any()))
                .thenReturn(false);

        assertThrows(ConditionsNotMetException.class,
                () -> itemService.addNewComment(itemId, userId, new CommentRequest()));
    }
}
