package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ShareItApp.class)
public class ShareItTest {

    @Test
    void contextLoads() {
    }
}
