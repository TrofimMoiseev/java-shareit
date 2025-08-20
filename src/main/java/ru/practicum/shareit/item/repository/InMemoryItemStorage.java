package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private Long sequenceTask = 0L;

    HashMap<Long, Item> items = new HashMap<>();

    protected Long getSequence() {
        sequenceTask++;
        return sequenceTask;
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return items.values().stream()
                .filter(x -> x.getOwnerId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

    }

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(x -> x.getName().toLowerCase().contains(text.toLowerCase()) ||
                        x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item post(Long userId, ItemDto itemDto) {
        Long itemId = getSequence();
        Item newItem = ItemMapper.toItem(userId, itemId, itemDto);
        items.put(itemId, newItem);
        return newItem;
    }

    @Override
    public Item put(Long itemId, Item newItem) {
        items.put(itemId, newItem);
        return newItem;
    }

    @Override
    public boolean checkId(Long id) {
        return !items.containsKey(id);
    }
}
