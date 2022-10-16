package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.modes.Create;
import ru.practicum.shareit.modes.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@SuperBuilder
public class UserDto {
    private Integer id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    @Email(groups = Create.class)
    @Email(groups = Update.class)
    private String email;
}