package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

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

    public static BookingDto toBookingDto(@NotNull Booking booking) {
        return new BookingDto(booking.getId(),
                        booking.getStart(),
                        booking.getEnd(),
                new BookingDto.User(booking.getBooker().getId()
                                //booking.getBooker().getName()),
                ),
                new BookingDto.Item(booking.getItem().getId(),
                                booking.getItem().getName()),
                        booking.getItem().getId(),
                        Status.getActualStatus(booking.getIsApproved(), booking.getIsCancelled()));

    }
}