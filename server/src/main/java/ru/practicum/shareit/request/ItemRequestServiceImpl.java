package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        checkUser(itemRequest.getRequestor().getId());
        User user = userService.getById(itemRequest.getRequestor().getId());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("добавлен request=/{}/", itemRequest);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllOwn(Integer requestorId) {
        checkUser(requestorId);
        List<ItemRequest> itemRequests = itemRequestRepository.getRequestsByRequestor(requestorId);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId()));
        }
        log.info("запрошены requests пользователя /{}/", requestorId);
        return itemRequests;
    }

    @Override
    public ItemRequest getById(Integer requestId, Integer requestorId) {
        checkUser(requestorId);
        log.info("запрошен request /{}/ пользователя /{}/", requestId, requestorId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId()));
        return itemRequest;
    }

    @Override
    public Collection<ItemRequest> getAll(Integer requestorId, Integer page, Integer size) {
        checkUser(requestorId);
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Collection<ItemRequest> itemRequests =
                itemRequestRepository.getAll(requestorId, PageRequest.of(page, size));
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId()));
        }
        return itemRequests;
    }

    private void checkUser(Integer userId) {
        userService.getById(userId);
    }
}
