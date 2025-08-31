package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemService {

    List<ItemDto> findAllByOwnerId(Long userId);

    ItemDto findById(Long itemId);

    List<ItemDto> getSearch(String text);

    ItemDto save(Long userId, Item item);

    ItemDto update(Long userId, Long itemId, ItemUpdateDto item);

    CommentDto addNewComment(Long itemId, Long userId, CommentRequest request);
}
