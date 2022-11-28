package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class BookingServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item = new Item();

    private final BookingDto bookingDto1 = new BookingDto();
    private final BookingDto bookingDto2 = new BookingDto();

    @BeforeEach
    public void init() {
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("u2@user.com");
        item.setId(1);
        item.setName("item1");
        item.setDescription("descr item1");
        item.setAvailable(true);
        item.setOwner(user1.getId());

        booking1.setId(1);
        booking1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        booking1.setBooker(2);
        booking1.setItem(1);
        booking1.setIsApproved(false);
        booking1.setIsCancelled(false);

        booking2.setId(2);
        booking2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        booking2.setBooker(2);
        booking2.setItem(1);
        booking2.setIsApproved(false);
        booking2.setIsCancelled(false);

        bookingDto1.setId(1);
        bookingDto1.setItemId(item.getId());
        bookingDto1.setBooker(new BookingDto.User(user2.getId()));
        bookingDto1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        bookingDto1.setStatus(Status.WAITING);
        bookingDto1.setItem(new BookingDto.Item(item.getId(), item.getName()));

        bookingDto2.setId(2);
        bookingDto2.setItemId(item.getId());
        bookingDto2.setBooker(new BookingDto.User(user2.getId()));
        bookingDto2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        bookingDto2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        bookingDto2.setStatus(Status.WAITING);
        bookingDto2.setItem(new BookingDto.Item(item.getId(), item.getName()));


    }

    @Test
    void canAdd() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.save(any()))
                .thenReturn(booking1);
        Assertions.assertEquals(bookingService.add(user2.getId(), bookingDto1).getId(), bookingDto1.getId());
        Assertions.assertEquals(bookingService.add(user2.getId(), bookingDto1).getItemId(), bookingDto1.getItemId());
    }

    @Test
    void cannotAddDueToPeriod() {
        bookingDto1.setStart(LocalDateTime.now().minusHours(2));
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        Assertions.assertThrows(ValidationException.class, () -> bookingService.add(user2.getId(), bookingDto1));
    }

    @Test
    void cannotAddDueToWrongUser() {
        bookingDto1.setBooker(new BookingDto.User(user1.getId()));
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.add(user1.getId(), bookingDto1));
    }

    @Test
    void findById() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        Assertions.assertEquals(bookingService.findById(1, 1).getId(), bookingDto1.getId());
        Assertions.assertEquals(bookingService.findById(1, 1).getItemId(), bookingDto1.getItemId());
    }

    @Test
    void approveBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking1);
        BookingDto result = bookingService.approveBooking(1, 1, true);
        Assertions.assertEquals(result.getId(), bookingDto1.getId());
        Assertions.assertEquals(result.getItemId(), bookingDto1.getItemId());
    }

    @Test
    void approveBookingReject() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking1);
        BookingDto result = bookingService.approveBooking(1, 1, false);
        Assertions.assertEquals(result.getId(), bookingDto1.getId());
        Assertions.assertEquals(result.getItemId(), bookingDto1.getItemId());
    }

    @Test
    void approveBookingFailWrongUser() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(2, 1, true));
    }

    @Test
    void findByBookerAll() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findAllByBookerId(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByBooker(2, State.ALL, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);

    }

    @Test
    void findByBookerWaiting() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findWaitingOrRejectedByBooker(anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByBooker(2, State.WAITING, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByBookerRejected() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        booking1.setIsCancelled(true);
        bookingDto1.setStatus(Status.REJECTED);
        when(bookingRepository.findWaitingOrRejectedByBooker(anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByBooker(2, State.REJECTED, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByBookerFuture() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findFutureByBooker(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByBooker(2, State.FUTURE, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByBookerPast() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        booking1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(2).withNano(0));
        bookingDto1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().minusHours(2).withNano(0));
        when(bookingRepository.findPastByBooker(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByBooker(2, State.PAST, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByBookerCurrent() {
        when(userService.getById(anyInt())).thenReturn(user2);
        when(itemService.findById(anyInt())).thenReturn(item);
        booking1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        bookingDto1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        when(bookingRepository.findCurrentByBooker(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByBooker(2, State.CURRENT, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByOwnerAll() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findAllByOwnerId(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByOwner(1, State.ALL, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByOwnerAllFail() {
        when(userService.getById(anyInt())).thenReturn(user1);
        Assertions.assertThrows(ResponseStatusException.class,
                () -> bookingService.findByOwner(1, State.ALL, -1, 0));
    }

    @Test
    void findByOwnerRejected() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        booking1.setIsCancelled(true);
        bookingDto1.setStatus(Status.REJECTED);
        when(bookingRepository.findWaitingOrRejectedByOwnerId(anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByOwner(1, State.REJECTED, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByOwnerWaiting() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findWaitingOrRejectedByOwnerId(anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByOwner(1, State.WAITING, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByOwnerFuture() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findFutureByOwnerId(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByOwner(2, State.FUTURE, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByOwnerPast() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        booking1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(2).withNano(0));
        bookingDto1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().minusHours(2).withNano(0));
        when(bookingRepository.findPastByOwnerId(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByOwner(2, State.PAST, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findByOwnerCurrent() {
        when(userService.getById(anyInt())).thenReturn(user1);
        when(itemService.findById(anyInt())).thenReturn(item);
        booking1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        bookingDto1.setStart(LocalDateTime.now().minusHours(1).withNano(0));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        when(bookingRepository.findCurrentByOwnerId(anyInt(), any())).thenReturn(List.of(booking1));
        List<BookingDto> result = bookingService.findByOwner(2, State.CURRENT, 0, 10);
        Assertions.assertEquals(List.of(bookingDto1), result);
    }

    @Test
    void findBookingOrException() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        Booking result = bookingService.findBookingOrException(1);
        Assertions.assertEquals(booking1, result);
    }

}