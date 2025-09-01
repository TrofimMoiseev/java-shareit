package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto findById(Long userId);

    UserDto save(User user);

    UserDto update(Long userId, User user);

    void delete(Long userId);
}
