package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserService {
    List<UserDto> getAll();

    User getById(Integer id) throws NotFoundException;

    UserDto add(User user);

    UserDto update(Integer id, User user) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;
}
