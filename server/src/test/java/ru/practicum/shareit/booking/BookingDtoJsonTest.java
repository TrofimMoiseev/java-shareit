package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDto(1L, LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0), null, null, Status.APPROVED);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(dto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(dto.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.item").isEqualTo(dto.getItem());
        assertThat(result).extractingJsonPathStringValue("$.booker").isEqualTo(dto.getBooker());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().toString());
    }

}
