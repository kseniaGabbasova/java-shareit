package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.data.annotation.Transient;
import ru.practicum.shareit.booking.Status;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDto.User booker;
    private BookingDto.Item item;
    @Transient
    private Integer itemId;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class User {
        private Integer id;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @NoArgsConstructor
    @Data
    public static class Item {
        private Integer id;
        private String name;
    }
}
