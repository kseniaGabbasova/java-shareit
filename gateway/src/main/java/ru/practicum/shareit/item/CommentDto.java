package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Integer id;

    @NotBlank(message = "содержание отзыва не может быть пустым")
    private String text;

    private Integer item;

    private Integer author;

    private String authorName;

}