package ru.practicum.shareit.item.comment;

import lombok.Data;

@Data
public class CommentDto {

    private Integer id;

    private String text;

    private Integer item;

    private Integer author;

    private String authorName;

}