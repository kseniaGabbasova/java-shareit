package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto add(Integer userId, BookingDto bookingDto) {
        Item item = itemService.findById(bookingDto.getItemId());
        User user = userService.getById(userId);
        validation(UserMapper.toUserDto(userService.getById(userId)), item, bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item.getId());
        booking.setBooker(user.getId());
        log.info("Бронирование от пользователя c id = {} к вещи с id = {} сохранено", userId, item.getId());
        return BookingMapper.toBookingDto(bookingRepository.save(booking), item);
    }

    @Override
    public BookingDto findById(Integer userId, Integer bookingId) {
        Booking booking = findBookingOrException(bookingId);
        User user = userService.getById(userId);
        Item item = itemService.findById(booking.getItem());
        if (!booking.getBooker().equals(user.getId())) {
            if (!item.getOwner().equals(user.getId())) {
                throw new NotFoundException("Недостаточно прав для выполнения операции.");
            }
        }
        log.info("Получение бронирования с id = {}", booking.getId());
        return BookingMapper.toBookingDto(booking, item);
    }

    @Override
    public BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Не указан статус.");
        }
        Booking booking = findBookingOrException(bookingId);
        User user = userService.getById(userId);
        Item item = itemService.findById(booking.getItem());
        approvalValidation(item, UserMapper.toUserDto(user), booking, approved);
        log.info("Бронирование с id = {} было одобрено", bookingId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking), item);
    }

    @Override
    @Transactional
    public List<BookingDto> findByBooker(Integer userId, State state, Integer from, Integer size) {
        userService.getById(userId);
        if (from < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return switchStateForBooker(userId, state, pageable);
    }

    @Override
    public List<BookingDto> findByOwner(Integer userId, State state, Integer from, Integer size) {
        userService.getById(userId);
        if (from < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        int page = from / size;
        Pageable pageRequest = PageRequest.of(page, size);
        return switchStateForOwner(userId, state, pageRequest);
    }

    @Override
    public Booking findBookingOrException(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        log.info("Поиск бронирования с id = {}", id);
        return booking.orElseThrow(() -> new NotFoundException("Бронирование с id " + id + " не найдено."));
    }

    private void approvalValidation(Item item, UserDto user, Booking booking, boolean approved) {
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

    private void validation(UserDto user, Item item, BookingDto bookingDto) {
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

    private List<BookingDto> switchStateForOwner(Integer userId, State state, Pageable pageRequest) {
        log.info("Получение бронирований пользователя с id = {} со статусом {}", userId, state);
        ArrayList<BookingDto> listDto = new ArrayList<>();
        switch (state) {
            case ALL:
                for (Booking booking : bookingRepository.findAllByOwnerId(userId,
                        pageRequest)) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                return listDto;
            case PAST:
                for (Booking booking : bookingRepository.findPastByOwnerId(userId, LocalDateTime.now())) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case CURRENT:
                for (Booking booking : bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now())) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case FUTURE:
                for (Booking booking : bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now())) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case WAITING:
                for (Booking booking : bookingRepository.findWaitingOrRejectedByOwnerId(userId,
                        false, false)) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case REJECTED:
                for (Booking booking : bookingRepository.findWaitingOrRejectedByOwnerId(userId,
                        false, true)) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            default:
                throw new UnsupportedStatus("Unknown state: " + state);
        }
        return listDto;
    }


    private List<BookingDto> switchStateForBooker(Integer userId, State state, Pageable pageRequest) {
        log.info("Получение бронирований пользователя с id = {} со статусом {}", userId, state);
        ArrayList<BookingDto> listDto = new ArrayList<>();
        switch (state) {
            case ALL:
                for (Booking booking : bookingRepository.findAllByBookerId(userId,
                        pageRequest)) {
                    Item item = itemService.findById(booking.getItem());
                    log.info(booking + "hhh");
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                    log.info(BookingMapper.toBookingDto(booking, item) + "jdjdjd");
                }
                return listDto;
            case PAST:
                for (Booking booking : bookingRepository.findPastByBooker(userId, LocalDateTime.now())) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case CURRENT:
                for (Booking booking : bookingRepository.findCurrentByBooker(userId, LocalDateTime.now())) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case FUTURE:
                for (Booking booking : bookingRepository.findFutureByBooker(userId, LocalDateTime.now())) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case WAITING:
                for (Booking booking : bookingRepository.findWaitingOrRejectedByBooker(userId,
                        false, false)) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            case REJECTED:
                for (Booking booking : bookingRepository.findWaitingOrRejectedByBooker(userId,
                        false, true)) {
                    Item item = itemService.findById(booking.getItem());
                    listDto.add(BookingMapper.toBookingDto(booking, item));
                }
                break;
            default:
                throw new UnsupportedStatus("Unknown state: " + state);
        }
        return listDto;
    }
}

