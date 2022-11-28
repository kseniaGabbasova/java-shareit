package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    BookingDto bookingDto = new BookingDto();
    private BookingDto.User user = new BookingDto.User();
    private BookingDto.User user1 = new BookingDto.User();
    private BookingDto.Item item = new BookingDto.Item();
    BookingDto bookingDto1 = new BookingDto();

    @BeforeEach
    void init() {
        user.setId(1);
        user1.setId(2);
        item.setId(1);
        item.setName("item1");
        bookingDto.setId(1);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setBooker(user1);
        bookingDto.setItem(item);
        bookingDto1.setId(2);
        bookingDto1.setStart(LocalDateTime.now().plusHours(5));
        bookingDto1.setEnd(LocalDateTime.now().plusHours(6));
        bookingDto1.setBooker(user1);
    }

    @Test
    void canAddSuccessfully() throws Exception {
        when(bookingService.add(anyInt(), any()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void approveBooking() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper
                        .writeValueAsString(bookingDto)));
    }

    @Test
    void findById() throws Exception {
        when(bookingService.findById(1, 2))
                .thenReturn(bookingDto1);
        mvc.perform(get("/bookings/{bookingId}", bookingDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper
                        .writeValueAsString(bookingDto1)));
    }

    @Test
    void findByUser() throws Exception {
        when(bookingService.findByBooker(anyInt(), any(),
                anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto1));
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto, bookingDto1))));
    }

    @Test
    void findBookingByOwner() throws Exception {
        when(bookingService.findByOwner(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto1));
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto, bookingDto1))));
    }
}