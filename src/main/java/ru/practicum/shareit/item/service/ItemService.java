package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemService {

    List<ItemOwnerDto> findAllByOwnerId(Long userId);

    ItemDto findById(Long itemId);

    List<ItemDto> getSearch(String text);

    ItemDto save(Long userId, Item item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    CommentDto addNewComment(Long itemId, Long userId, CommentRequest request);
}
