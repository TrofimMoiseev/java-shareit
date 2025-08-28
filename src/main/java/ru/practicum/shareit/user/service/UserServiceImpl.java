package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto get(Long userId) {
        if (userStorage.checkId(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Обработка GET-запроса на получение пользователя по id {}.", userId);
        return UserMapper.toUserDto(userStorage.get(userId));
    }

    @Override
    public UserDto post(UserDto userDto) {
        if (userStorage.checkEmail(userDto.getEmail())) {
            throw new ValidationException("Данный email уже используется");
        }
        log.info("Обработка POST-запроса на добавление пользователя {}.", userDto);
        return UserMapper.toUserDto(userStorage.post(userDto));
    }

    @Override
    public UserDto put(Long userId, UserDto userDto) {
        if (userStorage.checkId(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        User newUser = userStorage.get(userId);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            newUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()
                && userDto.getEmail().contains("@")) {
            if (userStorage.checkEmail(userDto.getEmail())) {
                throw new ValidationException("Данный email уже используется");
            }
            newUser.setEmail(userDto.getEmail());
        }
        log.info("Обработка PATH-запроса на обновление пользователя {} {}.", userId, userDto);
        return UserMapper.toUserDto(userStorage.put(userId, newUser));
    }

    @Override
    public void delete(Long userId) {
        log.info("Обработка DELETE-запроса на удаление пользователя {} .", userId);
        userStorage.delete(userId);
    }
}
