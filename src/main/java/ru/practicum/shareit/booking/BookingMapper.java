package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;

public class BookingMapper {

    public static Booking toBooking(@NotNull BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                null,
                null,
                false,
                false
        );
    }

    public static BookingDto toBookingDto(@NotNull Booking booking, Item item) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingDto.User(booking.getBooker()),
                new BookingDto.Item(item.getId(),
                        item.getName()),
                booking.getItem(),
                Status.getActualStatus(booking.getIsApproved(), booking.getIsCancelled()));

    }
}