package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam String text) {
        return itemService.getSearch(text);
    }

    @PostMapping
    public ItemDto post(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody @Valid ItemDto itemDto) {
        return itemService.post(userId, itemDto);
    }

    @PatchMapping ("/{itemId}")
    public ItemDto put(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId,
                       @RequestBody ItemDto itemDto) {
        return itemService.put(userId, itemId, itemDto);
    }
}
