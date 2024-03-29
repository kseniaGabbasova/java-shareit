package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemService;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("")
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id)
            throws NotFoundException {
        return itemService.getById(id, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody ItemDto itemDto) throws ValidationException {

        Item item;
        if (itemDto.getRequestId() != null) {
            item = ItemMapper.toItem(itemDto, userId, itemDto.getRequestId());
        } else {
            item = ItemMapper.toItem(itemDto, userId, null);
        }
        return itemService.add(item);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
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
    public List<ItemDto> getAllSearch(@RequestParam String text) {
        return itemService.search(text);

    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto, @PathVariable Integer itemId,
                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.addComment(commentDto, itemId, userId);
    }

}
