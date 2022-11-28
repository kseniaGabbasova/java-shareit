package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper();
    private final Item item = new Item();
    private final ItemDto itemDto = new ItemDto();

    @BeforeEach
    public void init() {
        item.setId(1);
        item.setOwner(1);
        item.setAvailable(true);
        item.setName("item");
        item.setDescription("descr of item");

        itemDto.setId(1);
        itemDto.setAvailable(true);
        itemDto.setName("item");
        itemDto.setDescription("descr of item");
    }

    @Test
    void toItem() {
        ItemDto result = itemMapper.toItemDto(item);
        Assertions.assertEquals(result.getId(), itemDto.getId());
        Assertions.assertEquals(result.getName(), itemDto.getName());
        Assertions.assertEquals(result.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getComments(), itemDto.getComments());
    }

    @Test
    void toItemDto() {
        Item result = itemMapper.toItem(itemDto, 1, 1);
        Assertions.assertEquals(result.getId(), item.getId());
        Assertions.assertEquals(result.getName(), item.getName());
        Assertions.assertEquals(result.getDescription(), item.getDescription());
        Assertions.assertEquals(result.getOwner(), item.getOwner());
    }
}