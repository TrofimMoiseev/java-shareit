package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private Long sequenceTask = 0L;

    HashMap<Long, User> users = new HashMap<>();

    protected Long getSequence() {
        sequenceTask++;
        return sequenceTask;
    }

    @Override
    public User get(Long userId) {
        return users.get(userId);
    }

    @Override
    public User post(UserDto userDto) {
        Long userId = getSequence();
        User newUser = UserMapper.toUser(userId, userDto);
        users.put(userId, newUser);
        log.info("Обновление репозитория, POST-запрос на добавление пользователя, userId {}.", userId);
        return newUser;
    }

    @Override
    public User put(Long userId, User user) {
        users.put(userId, user);
        log.info("Обновление репозитория, PATH-запрос на обновление пользователя, userId {}, user {}.", userId, user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean checkId(Long id) {
        return !users.containsKey(id);
    }

    public boolean checkEmail(String email) {
        return (users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email)));
    }
}
