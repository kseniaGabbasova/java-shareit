package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Integer id) throws NotFoundException {
        if (userRepository.existsById(id)) {
            return userRepository.getReferenceById(id);
        } else {
            throw new NotFoundException("Пользователя с id=" + id + " не существует");
        }
    }

    @Override
    public User add(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Integer id, User user) throws NotFoundException {
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
        return userToUpdate;
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        userRepository.delete(userRepository.getReferenceById(id));
    }
}
