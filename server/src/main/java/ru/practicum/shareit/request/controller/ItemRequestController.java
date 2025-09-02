package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
        private final ItemRequestService itemRequestService;

        @PostMapping
        public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestCreateDto dto) {
            return itemRequestService.addNewRequest(userId, dto);
        }

        @GetMapping
        public Collection<ItemRequestDto> findAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
            return itemRequestService.findAllRequestsByUserId(userId);
        }


        @GetMapping("/all")
        public Collection<ItemRequestDto> findAllRequestsByOtherUsers(
                @RequestHeader("X-Sharer-User-Id") Long userId,
                @PageableDefault(sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
            return itemRequestService.findAllRequestsByOtherUsers(userId, pageable)
                    .stream()
                    .map(ItemRequestMapper::mapToItemRequestDto)
                    .toList();
        }

        @GetMapping("/{requestId}")
        public ItemRequestDto findRequestById(@PathVariable Long requestId) {
            return itemRequestService.findRequestById(requestId);
        }
}
