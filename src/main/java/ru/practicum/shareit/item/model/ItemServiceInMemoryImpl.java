package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceInMemoryImpl implements ItemService {
    private Integer currId = 0;
    private final Map<Integer, Item> items = new HashMap<>();
    private final UserService userService;


    @Override
    public List<Item> getAllByOwner(Integer owner) {
        userService.getById(owner);
        List<Item> itemsByOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(item.getOwner(), owner)) {
                itemsByOwner.add(item);
            }
        }
        return itemsByOwner;
    }

    @Override
    public Item getById(Integer id) throws NotFoundException {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Предмета с id = " + id + " не существует");
        }
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());

    }

    @Override
    public Item add(Item item) {
        userService.getById(item.getOwner());
        item.setId(++currId);
        items.put(currId, item);
        return item;
    }

    @Override
    public Item update(Item item) throws NotFoundException {
        validateExistence(item);
        Item itemToUpdate = items.get(item.getId());
        validateOwner(item, itemToUpdate);
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        if (items.containsKey(id)) {
            items.remove(id);
        } else {
            throw new NotFoundException("Предмета с id = " + id + " не существует");
        }
    }

    @Override
    public List<Item> search(String text) {
        List<Item> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    foundItems.add(item);
                }
            }
        }
        return foundItems;
    }

    private void validateOwner(Item item, Item anotherItem) {
        if (!item.getOwner().equals(anotherItem.getOwner())) {
            throw new ForbiddenOperationException("Внести изменения может только обладатель");
        }
    }

    private void validateExistence(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Предмета с id = " + item.getId() + " не существует");
        }
    }
}

