package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.modes.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestBody @Validated({Create.class}) BookingDto bookingDto) {
        return bookingClient.add(userId, bookingDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return bookingClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return bookingClient.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approved(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer bookingId,
                                           @RequestParam boolean approved) {
        return bookingClient.approved(userId, bookingId, approved);
    }
}