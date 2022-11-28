package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item = new Item();

    @BeforeEach
    public void init() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        user2.setName("user2");
        user2.setEmail("u2@user.com");

        item.setName("item1");
        item.setDescription("descr item1");
        item.setAvailable(true);
        item.setOwner(1);

        booking1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        booking1.setBooker(2);
        booking1.setItem(1);
        booking1.setIsApproved(false);
        booking1.setIsCancelled(false);


        booking2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        booking2.setBooker(2);
        booking2.setItem(1);
        booking2.setIsApproved(false);
        booking2.setIsCancelled(false);


        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
        entityManager.persist(item);
        entityManager.flush();
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();
    }

    @Test
    void findAllByBookerId() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item and b.booker=:id and b.booker <> i.owner " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 2).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    void findCurrentByBooker() {

    }

    @Test
    void findPastByBooker() {
    }

    @Test
    void findFutureByBooker() {
        booking1.setStart(LocalDateTime.now().plusHours(1).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        booking2.setStart(LocalDateTime.now().plusHours(3).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(4).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where b.booker=:id and b.start>:date " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 2).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    void findWaitingByBooker() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where b.booker=:id " +
                        "and b.isApproved =:approve and b.isCancelled =:cancel order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 2).setParameter("approve", false)
                        .setParameter("cancel", false).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    void findRejectedByBooker() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where b.booker=:id " +
                        "and b.isApproved =:approve and b.isCancelled =:cancel order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("approve", false)
                        .setParameter("cancel", true).getResultList();
        Assertions.assertEquals(bookingResult, new ArrayList<>());
    }

    @Test
    void findAllByOwnerId() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item and i.owner=:id " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    void findPastByOwnerId() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(1).withNano(0));
        booking2.setStart(LocalDateTime.now().minusHours(4).withNano(0));
        booking2.setEnd(LocalDateTime.now().minusHours(3).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id and b.end<:date " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }

    @Test
    void findCurrentByOwnerId() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().plusHours(1).withNano(0));
        booking2.setStart(LocalDateTime.now().minusHours(4).withNano(0));
        booking2.setEnd(LocalDateTime.now().plusHours(3).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id and b.end>:date " +
                        "and b.start<:date order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }

    @Test
    void findFutureByOwnerId() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id and b.end>:date " +
                        "order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    void findWaitingByOwnerId() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id " +
                        "and b.isApproved =:approve and b.isCancelled =:cancel order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("approve", false)
                        .setParameter("cancel", false).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking2, booking1));
    }

    @Test
    void findRejectedByOwnerId() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b join Item i on i.id=b.item where i.owner=:id " +
                        "and b.isApproved =:approve and b.isCancelled =:cancel order by b.start desc", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("id", 1).setParameter("approve", false)
                        .setParameter("cancel", true).getResultList();
        Assertions.assertEquals(bookingResult, new ArrayList<>());
    }

    @Test
    void getLastBooking() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(1).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b join Item i on i.id=b.item and i.id=:id and b.end<:date",
                        Booking.class);
        Booking bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getSingleResult();
        Assertions.assertEquals(bookingResult.getBooker(), booking1.getBooker());
    }

    @Test
    void getNextBooking() {
        booking1.setStart(LocalDateTime.now().minusHours(2).withNano(0));
        booking1.setEnd(LocalDateTime.now().minusHours(1).withNano(0));
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b join Item i on i.id=b.item and i.id=:id " +
                        "and b.end>:date and b.start>:date", Booking.class);
        Booking bookingResult =
                query.setParameter("id", 1).setParameter("date", LocalDateTime.now()).getSingleResult();
        Assertions.assertEquals(bookingResult.getBooker(), booking2.getBooker());
    }

    @Test
    void getByBookerAndItem() {
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery(
                "select b from Booking b where b.booker=:booker and b.item=:item", Booking.class
        );
        List<Booking> bookingResult =
                query.setParameter("booker", 2).setParameter("item", 1).getResultList();
        Assertions.assertEquals(bookingResult, List.of(booking1, booking2));
    }
}