package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Item> getAll() {
        log.info("Получение всех вещей");
        return itemRepository.findAll();
    }

    @Override
    public Item findById(Integer id) {
        if (itemRepository.existsById(id)) {
            log.info("Получение вещи с id = {}", id);
            return itemRepository.getReferenceById(id);
        } else {
            throw new NotFoundException("Вещи с id = " + id + " не существует");
        }
    }

    @Override
    public ItemDto getById(Integer id, Integer userId) throws NotFoundException {
        if (itemRepository.existsById(id)) {
            Item item = findById(id);
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (item.getOwner().equals(userId)) {
                addBookings(itemDto);
            }
            addComments(itemDto);

            return itemDto;
        } else {
            throw new NotFoundException("Вещи с id=" + id + " не существует");
        }
    }

    @Override
    public List<ItemDto> getAllByOwner(Integer owner) throws NotFoundException {
        userService.getById(owner);
        List<ItemDto> list = itemRepository.findByOwner(owner)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получение всех вещей пользователя с id = {}", owner);
        for (ItemDto itemDto : list) {
            addBookings(itemDto);
            addComments(itemDto);
            log.info("Добавлены бронирования и комментарии для вещи с id = {}", itemDto.getId());
        }
        return list;
    }

    @Override
    public Item add(Item item) {
        userService.getById(item.getOwner());
        log.info("Добавление вещи {}", item);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Item item) throws NotFoundException {
        Item itemToUpdate = itemRepository.getReferenceById(item.getId());
        validateOwner(item, itemToUpdate);
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        log.info("Вещь с id = {} обноевлена", itemToUpdate.getId());
        return itemToUpdate;
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        log.info("Вещь с id = {} удалена", id);
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            log.info("В поле поиска был подан пустой текст");
            return new ArrayList<>();
        }
        String textForMethod = "%" + text + "%";
        log.info("Выполняется поиск по строке {}", text);
        return itemRepository.search(textForMethod);
    }

    private void validateOwner(Item item, Item anotherItem) {
        if (!item.getOwner().equals(anotherItem.getOwner())) {
            throw new ForbiddenOperationException("Внести изменения может только обладатель");
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Integer itemId, Integer userId) {
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemId);
        comment.setAuthor(userId);
        User user = userService.getById(userId);
        Collection<Booking> bookings =
                bookingRepository.getByBookerAndItem(comment.getAuthor(), comment.getItem());
        for (Booking booking : bookings) {
            if (booking != null && booking.getEnd().isBefore(LocalDateTime.now())
                    && !"".equals(comment.getText())) {
                commentRepository.save(comment);
                log.info("Комментарий с id = {} от пользователя с id = {} к вещи с id = {} был сохранен",
                        commentDto.getId(), userId, itemId);
                return CommentMapper.toDto(comment, user);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private void addBookings(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.getLastBooking(itemDto.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            itemDto.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        Booking nextBooking = bookingRepository.getNextBooking(itemDto.getId(), LocalDateTime.now());
        if (nextBooking != null) {
            itemDto.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
    }

    private void addComments(ItemDto itemDto) {
        List<ItemDto.Comment> comments = commentRepository.findAllByItem(itemDto.getId())
                .stream()
                .map(c -> new ItemDto.Comment(c.getId(), c.getText(), userService.getById(c.getAuthor()).getName(),
                        c.getCreated()))
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }

}
