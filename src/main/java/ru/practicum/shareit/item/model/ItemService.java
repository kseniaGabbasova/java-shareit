package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

public interface ItemService {
    List<Item> getAll();

    Item getById(Integer id) throws NotFoundException;

    List<Item> getAllByOwner(Integer owner) throws NotFoundException;

    Item add(Item item);

    Item update(Item item) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

    List<Item> search(String text);
}
