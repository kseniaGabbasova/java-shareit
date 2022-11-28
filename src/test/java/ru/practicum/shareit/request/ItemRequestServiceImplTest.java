package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ItemRequestServiceImplTest {
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final ItemRequest itemRequest = new ItemRequest();

    @BeforeEach
    void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        item1.setId(1);
        item1.setName("item1");
        item1.setDescription("descr item1");
        item1.setAvailable(true);
        item1.setOwner(user1.getId());
        item1.setRequestId(1);
        item2.setId(2);
        item2.setName("item2");
        item2.setDescription("descr item2");
        item2.setAvailable(true);
        item2.setOwner(user1.getId());
        item2.setRequestId(1);
        itemRequest.setId(1);
        itemRequest.setDescription("descr about the item1");
        itemRequest.setRequestor(user2);
        itemRequest.setCreated(LocalDateTime.now().withNano(0));
        itemRequest.setItems(Set.of(item1, item2));
    }

    @Test
    void add() {
        when(userService.getById(anyInt()))
                .thenReturn(user2);
        when(userService.getById(anyInt()))
                .thenReturn(user2);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        ItemRequest itemRequestResult = itemRequestService.add(itemRequest);
        Assertions.assertEquals(itemRequestResult, itemRequest);
    }

    @Test
    void getAllOwn() {
        when(userService.getById(anyInt()))
                .thenReturn(user2);
        when(itemRequestRepository.getRequestsByRequestor(anyInt()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyInt())).thenReturn(Set.of(item1, item2));
        List<ItemRequest> result = itemRequestService.getAllOwn(2);
        Assertions.assertEquals(List.of(itemRequest), result);
    }

    @Test
    void getById() {
        when(userService.getById(anyInt()))
                .thenReturn(user2);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyInt())).thenReturn(Set.of(item1, item2));
        ItemRequest result = itemRequestService.getById(1, 2);
        Assertions.assertEquals(itemRequest, result);
    }

    @Test
    void getAll() {
        when(userService.getById(anyInt()))
                .thenReturn(user2);
        when(itemRequestRepository.getAll(anyInt(), any()))
                .thenReturn((List.of(itemRequest)));
        when(itemRepository.findByRequestId(anyInt())).thenReturn(Set.of(item1, item2));
        Collection<ItemRequest> result = itemRequestService.getAll(2, 0, 10);
        Assertions.assertEquals(List.of(itemRequest), result);
    }
}