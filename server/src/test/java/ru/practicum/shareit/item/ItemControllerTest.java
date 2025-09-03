package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long itemId = 10L;

    @Test
    void getAll_shouldReturnItems() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Test item");

        Mockito.when(itemService.findAllByOwnerId(userId)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemId.intValue())))
                .andExpect(jsonPath("$[0].name", is("Test item")));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Item 1");

        Mockito.when(itemService.findById(itemId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Item 1")));
    }

    @Test
    void search_shouldReturnItems() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Drill");

        Mockito.when(itemService.getSearch("drill")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemId.intValue())))
                .andExpect(jsonPath("$[0].name", is("Drill")));
    }

    @Test
    void save_shouldReturnSavedItem() throws Exception {
        Item item = new Item();
        item.setId(itemId);
        item.setName("Hammer");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Hammer");

        Mockito.when(itemService.save(eq(userId), any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Hammer")));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Updated");

        ItemDto response = new ItemDto();
        response.setId(itemId);
        response.setName("Updated");

        Mockito.when(itemService.update(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    void addComment_shouldReturnCommentDto() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("Nice item!");

        CommentDto response = new CommentDto();
        response.setId(1L);
        response.setText("Nice item!");

        Mockito.when(itemService.addNewComment(eq(itemId), eq(userId), any(CommentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Nice item!")));
    }
}
