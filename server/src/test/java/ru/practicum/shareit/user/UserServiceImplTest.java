package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "Ivan", "ivan@example.com");
        userDto = new UserDto(1L, "Ivan", "ivan@example.com");
    }

    @Test
    void findById_shouldReturnUserDto_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void findById_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.findById(1L));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void save_shouldReturnSavedUserDto() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.save(user);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void update_shouldUpdateFieldsAndReturnDto() {
        User updateData = new User(null, "NewName", "new@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updated = userService.update(1L, updateData);

        assertEquals("NewName", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void update_shouldSkipNullFields() {
        User updateData = new User(null, null, null); // no changes
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updated = userService.update(1L, updateData);

        assertEquals(user.getName(), updated.getName());
        assertEquals(user.getEmail(), updated.getEmail());
    }

    @Test
    void update_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.update(1L, new User()));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
