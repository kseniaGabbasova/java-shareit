package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    @Autowired
    private UserServiceImpl userService;

    private User user1 = new User();
    private User user2 = new User();

    @BeforeEach
    public void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
    }

    @Test
    void getAll() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        List<UserDto> usersResult = userService.getAll();
        Assertions.assertEquals(2, usersResult.size());
        Assertions.assertEquals(List.of(UserMapper.toUserDto(user1), UserMapper.toUserDto(user2)), usersResult);
    }

    @Test
    void getById() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.getReferenceById(anyInt()))
                .thenReturn(user1);
        User result = userService.getById(1);
        Assertions.assertEquals(user1, result);
    }

    @Test
    void add() {
        when(userRepository.save(user1))
                .thenReturn(user1);
        UserDto newUser = userService.add(user1);
        Assertions.assertEquals(UserMapper.toUserDto(user1), newUser);
    }

    @Test
    void update() {
        when(userRepository.getReferenceById(anyInt()))
                .thenReturn(user1);
        User updUser = new User();
        updUser.setName("UpdUser");
        updUser.setEmail("upd@user.com");
        UserDto resultUser = userService.update(1, updUser);
        Assertions.assertEquals(1, resultUser.getId());
        Assertions.assertEquals(updUser.getName(), resultUser.getName());
        Assertions.assertEquals(updUser.getEmail(), resultUser.getEmail());
    }

    @Test
    void delete() {
        when(userRepository.getReferenceById(anyInt()))
                .thenReturn(user1);
        userService.delete(1);
        verify(userRepository, times(1))
                .delete(user1);
    }
}