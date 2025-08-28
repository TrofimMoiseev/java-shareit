package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<ItemDto> getAll(Long userId);

    Item get(Long itemId);

    List<ItemDto> getSearch(String text);

    Item post(Long userId, ItemDto itemDto);

    Item put(Long itemId, Item item);

    boolean checkId(Long id);
}
