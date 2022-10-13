package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.user.User;


public class CommentMapper {

    public static CommentDto toDto(Comment comment, User user) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem());
        commentDto.setAuthor(comment.getAuthor());
        commentDto.setAuthorName(user.getName());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(commentDto.getItem());
        comment.setAuthor(commentDto.getAuthor());
        return comment;
    }
}