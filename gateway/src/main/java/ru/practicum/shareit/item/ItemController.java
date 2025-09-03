package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdItemDto;

import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@Positive @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByUser(@Positive @PathVariable long itemId,
                                                @Positive @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getItemByUser(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Positive @RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(value = "text") String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchItems(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Positive @RequestHeader(USER_ID_HEADER) long userId,
                                             @Valid @RequestBody ItemDto dto) {
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long itemId,
                                             @Valid @RequestBody UpdItemDto updItemDto) {
        return itemClient.updateItem(userId, itemId, updItemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@Positive @RequestHeader(USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader(USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
