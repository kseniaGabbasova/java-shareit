package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Booking {
    private Integer id;
    private String start;
    private String end;
    private Integer item;
    private Integer booker;
    private Status status;
}