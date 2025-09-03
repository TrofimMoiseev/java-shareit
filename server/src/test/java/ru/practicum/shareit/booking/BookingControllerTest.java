package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.model.BookerState.ALL;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long bookingId = 100L;

    @Test
    void createBooking_shouldReturnBookingDto() throws Exception {
        BookingRequest request = new BookingRequest();
        BookingDto response = new BookingDto();
        response.setId(bookingId);

        Mockito.when(bookingService.createBooking(eq(userId), any(BookingRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())));
    }

    @Test
    void approveBooking_shouldReturnBookingDto() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(bookingId);

        Mockito.when(bookingService.approvedBooking(userId, bookingId, "true"))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())));
    }

    @Test
    void findBookingById_shouldReturnBookingDto() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(bookingId);

        Mockito.when(bookingService.findBookingById(bookingId, userId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())));
    }

    @Test
    void findBookingsByUserAndState_shouldReturnList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(bookingId);

        Mockito.when(bookingService.findBookingByUserAndState(userId, ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingId.intValue())));
    }

    @Test
    void findBookingsByOwner_shouldReturnList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(bookingId);

        Mockito.when(bookingService.findBookingByOwner(userId, ALL))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingId.intValue())));
    }
}
