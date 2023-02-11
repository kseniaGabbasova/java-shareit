package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Validated
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto user) {
        log.warn("Creating user");
        return userClient.addNewUser(user);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody @Valid UserDto user) {
        return userClient.update(id, user);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return userClient.delete(id);
    }
}