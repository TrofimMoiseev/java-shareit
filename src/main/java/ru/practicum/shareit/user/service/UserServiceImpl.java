package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Обработка GET-запроса на получение пользователя по id {}.", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto save(User user) {
        User newUser = userRepository.save(user);
        log.info("Обработка POST-запроса на добавление пользователя {}.", user);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto update(Long userId, User user) {

        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (user.getName() != null && !user.getName().isBlank()) {
            newUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            newUser.setEmail(user.getEmail());
        }
        userRepository.save(newUser);
        log.info("Обработка PATH-запроса на обновление пользователя {} {}.", userId, newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public void delete(Long userId) {
        log.info("Обработка DELETE-запроса на удаление пользователя {} .", userId);
        userRepository.deleteById(userId);
    }
}
