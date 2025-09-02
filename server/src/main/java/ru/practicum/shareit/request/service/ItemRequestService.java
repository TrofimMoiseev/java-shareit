package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addNewRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    Collection<ItemRequestDto> findAllRequestsByUserId(Long userId);

    Page<ItemRequest> findAllRequestsByOtherUsers(Long userId, Pageable pageable);

    ItemRequestDto findRequestById(Long requestId);
}
