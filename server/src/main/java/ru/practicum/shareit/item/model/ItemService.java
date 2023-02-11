package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    ItemDto getById(Integer id, Integer userId) throws NotFoundException;

    List<ItemDto> getAllByOwner(Integer owner) throws NotFoundException;

    ItemDto add(Item item);

    ItemDto update(Item item) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

    List<ItemDto> search(String text);

    CommentDto addComment(CommentDto commentDto, Integer itemId, Integer userId);

    Item findById(Integer id);

    List<Item> getAllByRequestIdOrderByIdAsc(Long requestId);
}
