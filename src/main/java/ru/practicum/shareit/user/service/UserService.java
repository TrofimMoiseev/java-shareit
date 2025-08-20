package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto get(Long userId);

    UserDto post(UserDto userDto);

    UserDto put(Long userId, UserDto userDto);

    void delete(Long userId);
}
