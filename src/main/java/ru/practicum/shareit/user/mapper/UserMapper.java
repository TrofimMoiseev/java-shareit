package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {
        public static UserDto toUserDto(User user) {
            return new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        }

        public static User toUser(Long userId, UserDto userDto) {
            return new User(
                    userId,
                    userDto.getName(),
                    userDto.getEmail()
            );
        }
}
