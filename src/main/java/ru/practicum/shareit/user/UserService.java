package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;


public interface UserService {
    List<User> getAll();

    User getById(Integer id) throws NotFoundException;

    User add(User user);

    User update(Integer id, User user) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;
}
