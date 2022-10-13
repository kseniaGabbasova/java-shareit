package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

public class UserServiceInMemoryImpl implements UserService {
    private Integer currId = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Integer id) throws NotFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователя с id=" + id + " не существует");
        }
    }

    @Override
    public User add(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("Email: " + user.getEmail() + " уже используется");
        }
        user.setId(++currId);
        users.put(currId, user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Integer id, User user) throws NotFoundException {
        if (users.containsKey(id)) {
            User userToUpdate = users.get(id);
            if (user.getName() != null) {
                userToUpdate.setName(user.getName());
            }
            if (user.getEmail() != null) {
                if (!emails.contains(user.getEmail())) {
                    emails.remove(userToUpdate.getEmail());
                    userToUpdate.setEmail(user.getEmail());
                    emails.add(user.getEmail());
                } else {
                    throw new RuntimeException("Адрес занят другим пользователем");
                }
            }
            return userToUpdate;
        } else {
            throw new NotFoundException("Пользователя с id=" + id + " не существует");
        }
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
