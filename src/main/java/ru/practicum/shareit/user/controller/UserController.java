package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Получен GET-запроса на получение пользователя по id {}.", userId);
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto save(@RequestBody @Valid User user) {
        log.info("Получен POST-запроса на добавление пользователя {}.", user);
        return userService.save(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
    @RequestBody User user) {
        log.info("Получен PATH-запроса на обновление пользователя {} {}.", userId, user);
        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Получен DELETE-запроса на удаление пользователя {} .", userId);
        userService.delete(userId);
    }
}
