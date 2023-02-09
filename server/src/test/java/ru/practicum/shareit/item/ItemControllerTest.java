package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(CommentMapper.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDto = new ItemDto();
    private ItemDto itemDto1 = new ItemDto();
    private ItemDto itemDtoUpd = new ItemDto();

    private User user = new User();

    @BeforeEach
    public void init() {
        user.setId(1);
        user.setName("user1");
        user.setEmail("u1@user.com");

        itemDto.setName("item1");
        itemDto.setDescription("descr of item1");
        itemDto.setAvailable(true);

        itemDtoUpd.setName("item1");
        itemDtoUpd.setDescription("descr of item1");
        itemDtoUpd.setAvailable(true);
    }

    @Test
    void canGetAllFromOwner() throws Exception {
        when(itemService.getAllByOwner(anyInt()))
                .thenReturn(List.of(itemDto, itemDto1));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto, itemDto1))));
    }

    @Test
    void getById() throws Exception {
        when(itemService.getById(anyInt(), any()))
                .thenReturn(itemDtoUpd);
        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoUpd)))
                .andExpect(jsonPath("$.name", is(itemDtoUpd.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpd.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpd.getAvailable())));
    }

    @Test
    void create() throws Exception {
        when(itemService.add(any()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    void update() throws Exception {
        itemDto.setId(1);
        itemDto.setName("updItem");
        itemDto.setDescription("descr of updItem");
        when(itemService.update(any()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/{id}", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void canDelete() throws Exception {
        mvc.perform(delete("/items/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito
                .verify(itemService, Mockito.times(1))
                .delete(1);
    }

    @Test
    void addComment() throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor(1);
        comment.setItem(1);
        comment.setText("comment about item1");
        CommentDto commentDto = CommentMapper.toDto(comment, user);
        when(itemService.addComment(any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId())))
                .andExpect(jsonPath("$.author", is(comment.getAuthor())))
                .andExpect(jsonPath("$.item", is(comment.getItem())))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }

    @Test
    void canPerformSearch() throws Exception {
        when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto));
        String strSearch = "DESCR";
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", strSearch)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }
}