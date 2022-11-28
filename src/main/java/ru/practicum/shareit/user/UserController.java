package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private final UserService userService;

    @GetMapping("")
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public UserDto getById(@PathVariable Integer id) throws NotFoundException {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return userService.add(user);
    }

    @PatchMapping("{id}")
    public UserDto update(@PathVariable Integer id,
                       @Validated(Update.class) @RequestBody UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        return userService.update(id, user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }
}
