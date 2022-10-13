package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto add(Integer userId, BookingDto bookingDto) {
        Item item = itemService.findById(bookingDto.getItemId());
        User user = userService.getById(userId);
        validation(userService.getById(userId), item, bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Integer userId, Integer bookingId) {
        Booking booking = findBookingOrException(bookingId);
        User user = userService.getById(userId);
        if (!booking.getBooker().getId().equals(user.getId())) {
            if (!booking.getItem().getOwner().equals(user.getId())) {
                throw new NotFoundException("Недостаточно прав для выполнения операции.");
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Не указан статус.");
        }
        Booking booking = findBookingOrException(bookingId);
        User user = userService.getById(userId);
        Item item = itemService.findById(booking.getItem().getId());
        approvalValidation(item, user, booking, approved);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> findByBooker(Integer userId, State state) {
        userService.getById(userId);
        return switchStateForBooker(userId, state);
    }

    @Override
    public List<BookingDto> findByOwner(Integer userId, State state) {
        userService.getById(userId);
        return switchStateForOwner(userId, state);
    }

    @Override
    public Booking findBookingOrException(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElseThrow(() -> new NotFoundException("Бронирование с id " + id + " не найдено."));
    }

    private void approvalValidation(Item item, User user, Booking booking, boolean approved) {
        if (!item.getOwner().equals(user.getId())) {
            throw new NotFoundException("Недостаточно прав для выполнения операции.");
        }
        if (booking.getIsApproved() && !booking.getIsCancelled() && approved) {
            throw new ValidationException("Статус 'Подтвержден' уже установлен.");
        }
        if (approved) {
            booking.setIsApproved(true);
            booking.setIsCancelled(false);
        } else {
            booking.setIsApproved(false);
            booking.setIsCancelled(true);
        }
    }

    private void validation(User user, Item item, BookingDto bookingDto) {
        if (user.getId().equals(item.getOwner())) {
            throw new NotFoundException("Владелец вещи не может быть ее арендатором.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + item + " не доступна для бронирования.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Неверно указан период бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неверно указан период бронирования.");
        }
    }

    private List<BookingDto> switchStateForOwner(Integer userId, State state) {
        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findPastByOwnerId(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findWaitingOrRejectedByOwnerId(userId,
                                false, false)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findWaitingOrRejectedByOwnerId(userId,
                                false, true)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatus("Unknown state: " + state);
        }
    }

    private List<BookingDto> switchStateForBooker(Integer userId, State state) {
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findPastByBooker(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentByBooker(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findFutureByBooker(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findWaitingOrRejectedByBooker(userId,
                                false, false)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findWaitingOrRejectedByBooker(userId,
                                false, true)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatus("Unknown state: " + state);
        }
    }
}
