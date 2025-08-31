package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam String text) {
        return itemService.getSearch(text);
    }

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody @Valid Item item) {
        return itemService.save(userId, item);
    }

    @PatchMapping ("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId,
                       @RequestBody Item item) {
        return itemService.update(userId, itemId, item);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentRequest request) {
        return itemService.addNewComment(itemId, userId, request);
    }
}
