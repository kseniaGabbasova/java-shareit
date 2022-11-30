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
    void toItemDto() {
        ItemDto result = itemMapper.toItemDto(item);
        Assertions.assertEquals(itemDto.getId(), result.getId());
        Assertions.assertEquals(itemDto.getName(), result.getName());
        Assertions.assertEquals(itemDto.getDescription(), result.getDescription());
        Assertions.assertEquals(itemDto.getComments(), result.getComments());
    }

    @Test
    void toItem() {
        Item result = itemMapper.toItem(itemDto, 1, 1);
        Assertions.assertEquals(item.getId(), result.getId());
        Assertions.assertEquals(item.getName(), result.getName());
        Assertions.assertEquals(item.getDescription(), result.getDescription());
        Assertions.assertEquals(item.getOwner(), result.getOwner());
    }
}