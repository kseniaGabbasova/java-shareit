package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.modes.Create;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("")
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("{id}")
    public Item getById(@PathVariable Integer id) throws NotFoundException {
        return itemService.getById(id);
    }

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @Validated(Create.class) @RequestBody ItemDto itemDto) throws ValidationException {
        Item item = ItemMapper.toItem(itemDto, userId, null);
        return itemService.add(item);
    }

    @PatchMapping("{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @PathVariable Integer id,
                       @RequestBody ItemDto itemDto) throws ValidationException {
        Item item = ItemMapper.toItem(itemDto, userId, null);
        item.setId(id);
        return itemService.update(item);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        itemService.delete(id);
    }

    @GetMapping("search")
    public List<Item> getAll(@RequestParam String text) {
        return itemService.search(text);

    }

}
