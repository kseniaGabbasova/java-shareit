package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.data.annotation.Transient;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.modes.Create;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Integer id;
    @FutureOrPresent
    private LocalDateTime start;
    @FutureOrPresent
    private LocalDateTime end;
    @NotBlank(groups = Create.class)
    private BookingDto.User booker;
    @NotBlank(groups = Create.class)
    private BookingDto.Item item;
    @Transient
    private Integer itemId;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Integer id;
        //private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Item {
        private Integer id;
        private String name;
    }
}
