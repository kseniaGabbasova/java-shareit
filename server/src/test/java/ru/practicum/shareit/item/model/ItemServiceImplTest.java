package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final ItemDto itemDto1 = new ItemDto();
    private final ItemDto itemDto2 = new ItemDto();
    private final Comment comment = new Comment();
    private final CommentDto commentDto = new CommentDto();
    private final Item item1 = new Item();
    private final Item item2 = new Item();

    @BeforeEach
    void init() {
        item1.setId(1);
        item1.setAvailable(true);
        item1.setName("item1");
        item1.setDescription("descr of item1");
        item1.setOwner(1);
        item2.setId(2);
        item2.setAvailable(true);
        item2.setName("item2");
        item2.setDescription("descr of item2");
        item2.setOwner(1);
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        booking1.setId(1);
        booking1.setItem(1);
        booking1.setIsApproved(false);
        booking1.setIsCancelled(false);
        booking1.setBooker(1);
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        booking2.setId(2);
        booking2.setItem(2);
        booking2.setIsApproved(false);
        booking2.setIsCancelled(false);
        booking2.setBooker(1);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setEnd(LocalDateTime.now().minusHours(2));
        itemDto1.setId(1);
        itemDto1.setAvailable(true);
        itemDto1.setName(item1.getName());
        itemDto1.setDescription(item1.getDescription());
        itemDto1.setLastBooking(new ItemDto.Booking(booking1.getId(), booking1.getBooker()));
        itemDto1.setNextBooking(new ItemDto.Booking(booking1.getId(), booking1.getBooker()));
        itemDto1.setComments(new ArrayList<>());
        itemDto2.setId(2);
        itemDto2.setAvailable(true);
        itemDto2.setName(item2.getName());
        itemDto2.setDescription(item2.getDescription());
        itemDto2.setLastBooking(new ItemDto.Booking(booking1.getId(), booking1.getBooker()));
        itemDto2.setNextBooking(new ItemDto.Booking(booking1.getId(), booking1.getBooker()));
        itemDto2.setComments(new ArrayList<>());
        comment.setId(1);
        comment.setAuthor(2);
        comment.setItem(1);
        comment.setCreated(LocalDateTime.now());
        comment.setText("comment on item1");
        commentDto.setId(1);
        commentDto.setAuthor(2);
        commentDto.setAuthorName(user2.getName());
        commentDto.setItem(1);
        commentDto.setText("comment on item1");
    }

    @Test
    void getAll() {
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));
        List<Item> result = itemService.getAll();
        Assertions.assertEquals(List.of(item1, item2), result);
    }

    @Test
    void findById() {
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.getReferenceById(anyInt())).thenReturn(item1);
        Item result = itemService.findById(1);
        Assertions.assertEquals(item1, result);
    }

    @Test
    void getById() {
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.getReferenceById(anyInt())).thenReturn(item1);
        when(bookingRepository.getLastBooking(anyInt(), any())).thenReturn(booking1);
        when(bookingRepository.getNextBooking(anyInt(), any())).thenReturn(booking1);
        ItemDto result = itemService.getById(1, 1);
        Assertions.assertEquals(itemDto1, result);
    }

    @Test
    void getAllByOwner() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemRepository.findByOwnerOrderByIdAsc(anyInt())).thenReturn(List.of(item1, item2));
        when(bookingRepository.getLastBooking(anyInt(), any())).thenReturn(booking1);
        when(bookingRepository.getNextBooking(anyInt(), any())).thenReturn(booking1);
        List<ItemDto> result = itemService.getAllByOwner(1);
        Assertions.assertEquals(List.of(itemDto1, itemDto2), result);
    }

    @Test
    void add() {
        itemDto1.setLastBooking(null);
        itemDto1.setNextBooking(null);
        itemDto1.setComments(null);
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemRepository.save(any())).thenReturn(item1);
        ItemDto result = itemService.add(item1);
        Assertions.assertEquals(itemDto1, result);
    }

    @Test
    void update() {
        itemDto2.setId(1);
        itemDto2.setLastBooking(null);
        itemDto2.setNextBooking(null);
        itemDto2.setComments(null);
        item2.setId(1);
        when(itemRepository.getReferenceById(anyInt())).thenReturn(item1);
        ItemDto result = itemService.update(item2);
        Assertions.assertEquals(itemDto2, result);
    }

    @Test
    void updateFail() {
        itemDto2.setId(1);
        itemDto2.setLastBooking(null);
        itemDto2.setNextBooking(null);
        itemDto2.setComments(null);
        item2.setId(1);
        when(itemRepository.getReferenceById(anyInt())).thenReturn(item1);
        item2.setOwner(5);
        Assertions.assertThrows(ForbiddenOperationException.class, () -> itemService.update(item2));
    }

    @Test
    void search() {
        itemDto2.setLastBooking(null);
        itemDto2.setNextBooking(null);
        itemDto2.setComments(null);
        itemDto1.setLastBooking(null);
        itemDto1.setNextBooking(null);
        itemDto1.setComments(null);
        when(itemRepository.search(anyString())).thenReturn(List.of(item1, item2));
        List<ItemDto> result = itemService.search("descr");
        Assertions.assertEquals(List.of(itemDto1, itemDto2), result);
    }

    @Test
    void addComment() {
        commentDto.setId(null);
        when(userService.getById(anyInt())).thenReturn(user2);
        when(bookingRepository.getByBookerAndItem(anyInt(), anyInt())).thenReturn(List.of(booking1));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto result = itemService.addComment(commentDto, 1, 2);
        Assertions.assertEquals(commentDto, result);
    }
}