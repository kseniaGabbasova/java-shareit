package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(Integer userId, BookingDto bookingDto);

    BookingDto findById(Integer userId, Integer bookingId);

    BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved);

    List<BookingDto> findByBooker(Integer userId, State state, Integer from, Integer size);

    List<BookingDto> findByOwner(Integer userId, State state, Integer from, Integer size);

    Booking findBookingOrException(Integer id);
}
