package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    //@ResponseStatus(CREATED)
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody BookingDto bookingDto) {
        return service.add(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    //@ResponseStatus(OK)
    public BookingDto approveBooking(@PathVariable Integer bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestParam Boolean approved) {
        return service.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @PathVariable Integer bookingId) {
        return service.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(defaultValue = "ALL") State state) {
        return service.findByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(defaultValue = "ALL") State state) {
        return service.findByOwner(userId, state);
    }
}
