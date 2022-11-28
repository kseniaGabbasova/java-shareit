package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.modes.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Integer requestId;
    private ItemDto.Booking lastBooking;
    private ItemDto.Booking nextBooking;
    private List<Comment> comments;

    @Data
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class Booking {
        private Integer id;
        private Integer bookerId;
    }

    @Data
    @AllArgsConstructor
    public static class Comment {
        private Integer id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}
