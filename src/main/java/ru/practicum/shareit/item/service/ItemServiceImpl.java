package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> getAll(Long userId) {
        if(userStorage.checkId(userId)) {
        throw new NotFoundException("Пользователя не существует");
        }

        return itemStorage.getAll(userId);
    }

    @Override
    public ItemDto get(Long itemId) {
        if(itemStorage.checkId(itemId)) {
            throw new NotFoundException("Предмет не найден");
        }

        return ItemMapper.toItemDto(itemStorage.get(itemId));
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.getSearch(text);
    }

    @Override
    public ItemDto post(Long userId, ItemDto itemDto) {
        if(userStorage.checkId(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        log.info("Обработка POST-запроса на добавление предмета {}.", itemDto);
        return ItemMapper.toItemDto(itemStorage.post(userId, itemDto));
    }

    @Override
    public ItemDto put(Long userId, Long itemId, ItemDto itemDto) {
        if(itemStorage.checkId(itemId)) {
            throw new NotFoundException("Предмет не найден");
        }

        Item newItem = itemStorage.get(itemId);

        if(!newItem.getOwnerId().equals(userId)) {
            throw new NotFoundException("Вы не являетесь владельцем предмета");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            newItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            newItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.put(itemId, newItem));
    }
}
