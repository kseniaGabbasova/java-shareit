package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Integer id;

    @NotBlank(message = "содержание отзыва не может быть пустым")
    private String text;

    //@NotBlank(message = "id вещи не может быть пустым")
    private Integer item;

    //@NotBlank(message = "id автора не может быть пустым")
    private Integer author;

    //@NotBlank(message = "имя автора не может быть пустым")
    private String authorName;

}