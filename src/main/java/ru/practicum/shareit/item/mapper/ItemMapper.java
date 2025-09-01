package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemOwnerDto toItemDtoForOwner(Item item,
                                                 BookingShortDto lastBooking,
                                                 BookingShortDto nextBooking,
                                                 List<CommentDto> comments) {
        return new ItemOwnerDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments != null ? comments : List.of()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                List.of()
        );
    }

    public static ItemDto toItemDtoWithComments(Item item,
                                                List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments != null ? comments : List.of()
        );
    }


}
