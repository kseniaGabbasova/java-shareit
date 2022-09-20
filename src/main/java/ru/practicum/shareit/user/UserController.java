package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.modes.Create;
import ru.practicum.shareit.modes.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public User getById(@PathVariable Integer id) throws NotFoundException {
        return userService.getById(id);
    }

    @PostMapping
    public User create(@Validated(Create.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return userService.add(user);
    }

    @PatchMapping("{id}")
    public User update(@PathVariable Integer id,
                       @Validated(Update.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return userService.update(id, user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }
}
