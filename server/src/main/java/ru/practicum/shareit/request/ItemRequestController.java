package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestBody ItemRequestDto itemRequestDto,
                              @RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestorId);
        return ItemRequestMapper.toDto(itemRequestService.add(itemRequest));
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        return ItemRequestMapper.getAllOwn(requestorId, itemRequestService);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Integer requestId,
                                  @RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        return ItemRequestMapper.toDto(itemRequestService.getById(requestId, requestorId));
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {

        return ItemRequestMapper.getAll(requestorId, from, size, itemRequestService);
    }
}
