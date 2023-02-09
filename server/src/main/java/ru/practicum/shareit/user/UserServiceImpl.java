package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> result = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            result.add(UserMapper.toUserDto(u));
        }
        return result;
    }

    @Override
    public User getById(Integer id) throws NotFoundException {
        if (userRepository.existsById(id)) {
            log.info("Получение пользователя с id = {}", id);
            return userRepository.getReferenceById(id);
        } else {
            throw new NotFoundException("Пользователя с id=" + id + " не существует");
        }
    }

    @Override
    public UserDto add(UserDto userDto) {
        if (userRepository.getAllEmails().contains(userDto.getEmail())) {
            throw new InternalError();
        }
        User user = UserMapper.toUser(userDto);
        log.info("Добавление пользователя {}", user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Integer id, User user) throws NotFoundException {
        User userToUpdate = userRepository.getReferenceById(id);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!userRepository.getAllEmails().contains(user.getEmail())) {
                userToUpdate.setEmail(user.getEmail());
            } else {
                throw new InternalError();
            }
        }
        log.info("Пользователь с id = {} обновлен", id);
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        log.info("Пользователь с id = {} удален", id);
        userRepository.delete(userRepository.getReferenceById(id));
    }
}
