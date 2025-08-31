package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> findAllByOwnerId(Long userId) {
        if (userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }


    @Override
    public ItemDto save(Long userId, Item item) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        item.setId(userId);
        Item newItem = itemRepository.save(new Item());

        log.info("Обработка POST-запроса на добавление предмета {}.", item);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, Item item) {

        Item updItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с заданным ID не найдена"));
        if (!updItem.getOwnerId().equals(userId)) {
            throw new NotFoundException("Вносить изменения в поля может только владелец");
        }
        if (item.getName() != null) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }
        itemRepository.save(updItem);

        return ItemMapper.toItemDto(updItem);
    }

    @Transactional
    public CommentDto addNewComment(Long itemId, Long userId, CommentRequest request) {
        log.debug("Запрашиваем пользователя по ID {}", userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным ID не найден"));
        log.debug("Запрашиваем вещь по ID {}", userId);
        Item commentedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена"));
        Comment comment = CommentMapper.toComment(request, author, commentedItem);
        log.debug("Комментарий: {}", comment);
        if (!bookingRepository.existsPastBookingExcludingRejected(userId,
                itemId,
                LocalDateTime.now())) {
            throw new ValidationException("Нужно создать бронирование, только потом комментарий");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
