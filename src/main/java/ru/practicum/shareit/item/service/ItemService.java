package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    List<ItemDto> getAll(Long userId);

    ItemDto get(Long itemId);

    List<ItemDto> getSearch(String text);

    ItemDto post(Long userId, ItemDto itemDto);

    ItemDto put(Long userId, Long itemId, ItemDto itemDto);
}
