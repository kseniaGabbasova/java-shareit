//package ru.practicum.shareit.request;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.request.dto.ItemRequestDto;
//import ru.practicum.shareit.user.User;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Set;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ItemRequestController.class)
//@Import(ItemRequestMapper.class)
//class ItemRequestControllerTest {
//
//    @MockBeana
//    private ItemRequestService itemRequestService;
//    @Autowired
//    private MockMvc mvc;
//    @Autowired
//    private ObjectMapper mapper;
//
//    private User user1 = new User();
//    private User user2 = new User();
//    private Item item1 = new Item();
//    private Item item2 = new Item();
//    private ItemRequest itemRequest = new ItemRequest();
//    private ItemRequestDto itemRequestDto = new ItemRequestDto();
//
//    @BeforeEach
//    void init() {
//        user1.setId(1);
//        user1.setName("user1");
//        user1.setEmail("u1@user.com");
//        user2.setId(2);
//        user2.setName("user2");
//        user2.setEmail("u2@user.com");
//        item1.setId(1);
//        item1.setName("item1");
//        item1.setDescription("descr item1");
//        item1.setAvailable(true);
//        item1.setOwner(user1.getId());
//        item2.setId(2);
//        item2.setName("item2");
//        item2.setDescription("descr item2");
//        item2.setAvailable(true);
//        item2.setOwner(user1.getId());
//        itemRequest.setId(1);
//        itemRequest.setDescription("descr about the item1");
//        itemRequest.setRequestor(user2);
//        itemRequest.setItems(Set.of(item1, item2));
//        itemRequest.setCreated(LocalDateTime.now().withNano(0));
//        itemRequestDto.setId(1);
//        itemRequestDto.setDescription("descr about the item1");
//        itemRequestDto.setRequestor(user2);
//        itemRequestDto.setItems(Set.of(item1, item2));
//        itemRequestDto.setCreated(LocalDateTime.now().withNano(0));
//    }
//
//    @Test
//    void add() throws Exception {
//        when(itemRequestService.add(any()))
//                .thenReturn(itemRequest);
//        mvc.perform(post("/requests")
//                        .content(mapper.writeValueAsString(itemRequestDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemRequestDto.getId())))
//                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
//                .andExpect(jsonPath("$.items", is(notNullValue())));
//    }
//
//    @Test
//    void getAllOwn() throws Exception {
//        when(itemRequestService.getAllOwn(anyInt()))
//                .thenReturn(List.of(itemRequest));
//        mvc.perform(get("/requests")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.*", is(hasSize(1))))
//                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Integer.class))
//                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
//                .andExpect(jsonPath("$.[0].created", is(notNullValue())))
//                .andExpect(jsonPath("$.[0].items", is(notNullValue())));
//    }
//
//    @Test
//    void getById() throws Exception {
//        when(itemRequestService.getById(anyInt(), anyInt()))
//                .thenReturn(itemRequest);
//        mvc.perform(get("/requests/{requestId}", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
//                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
//                .andExpect(jsonPath("$.created", is(notNullValue())))
//                .andExpect(jsonPath("$.items", is(notNullValue())));
//    }
//
//    @Test
//    void getAll() throws Exception {
//        when(itemRequestService.getAll(anyInt(), anyInt(), anyInt()))
//                .thenReturn(List.of(itemRequest));
//        mvc.perform(get("/requests/all")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 2))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.*", is(hasSize(1))))
//                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Integer.class))
//                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
//                .andExpect(jsonPath("$.[0].created", is(notNullValue())))
//                .andExpect(jsonPath("$.[0].items", is(notNullValue())));
//    }
//}