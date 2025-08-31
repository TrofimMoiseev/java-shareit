package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }


    public static List<CommentDto> mapCommentToDtoList(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    public static Comment toComment(CommentRequest comment, User user, Item item) {
        Comment newComment = new Comment();
        newComment.setText(comment.getText());
        newComment.setAuthor(user);
        newComment.setItem(item);
        newComment.setCreated(LocalDateTime.now());
        return newComment;
    }
}
