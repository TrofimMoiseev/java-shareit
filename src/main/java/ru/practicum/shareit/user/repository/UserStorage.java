package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    User get(Long userId);

    User post(UserDto userDto);

    User put(Long userId, User user);

    void delete(Long userId);

    boolean checkId(Long id);

    boolean checkEmail(String email);
}
