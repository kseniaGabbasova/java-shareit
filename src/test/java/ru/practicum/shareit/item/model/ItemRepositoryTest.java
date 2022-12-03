package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;

import javax.persistence.TypedQuery;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    private final Item item1 = new Item();
    private final User user1 = new User();

    @Test
    void search() {
        user1.setName("user1");
        user1.setEmail("u1@user.com");
        entityManager.persist(user1);
        item1.setName("item1");
        item1.setDescription("desc Item1");
        item1.setAvailable(true);
        item1.setOwner(1);
        entityManager.persist(item1);
        TypedQuery<Item> query = entityManager.getEntityManager()
                .createQuery("SELECT i FROM Item i WHERE upper(i.name) like upper(concat('%',:str,'%'))" +
                        " OR upper(i.description) like upper(concat('%',:str,'%')) ", Item.class);
        List<Item> items = query.setParameter("str", "item").getResultList();
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item1, items.toArray()[0]);
    }

}