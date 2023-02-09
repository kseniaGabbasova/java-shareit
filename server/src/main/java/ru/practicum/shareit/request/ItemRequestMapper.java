package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;

public class ItemRequestMapper {
    public static Collection<ItemRequestDto> getAllOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                                       ItemRequestService itemRequestService) {
        Collection<ItemRequest> itemRequests = itemRequestService.getAllOwn(requestorId);
        Collection<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(ItemRequestMapper.toDto(itemRequest));
        }
        return itemRequestDtos;
    }

    public static Collection<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                                    ItemRequestService itemRequestService) {
        Collection<ItemRequest> itemRequests = itemRequestService.getAll(requestorId, from, size);
        Collection<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(ItemRequestMapper.toDto(itemRequest));
        }
        return itemRequestDtos;
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(itemRequest.getItems());
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Integer requestorId) {
        ItemRequest itemRequest = new ItemRequest();
        User user = new User();
        user.setId(requestorId);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        return itemRequest;
    }

}
