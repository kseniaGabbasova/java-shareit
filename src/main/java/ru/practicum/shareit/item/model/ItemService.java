package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    ItemDto getById(Integer id, Integer userId) throws NotFoundException;

    List<ItemDto> getAllByOwner(Integer owner) throws NotFoundException;

    Item add(Item item);

    Item update(Item item) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

    List<Item> search(String text);

    CommentDto addComment(CommentDto commentDto, Integer itemId, Integer userId);

    Item findById(Integer id);
}
