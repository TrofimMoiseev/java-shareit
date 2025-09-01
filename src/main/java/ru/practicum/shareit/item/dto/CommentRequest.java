package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 1000, message = "Комментарий должен содержать от 1 до 1000 символов")
    private String text;
}
