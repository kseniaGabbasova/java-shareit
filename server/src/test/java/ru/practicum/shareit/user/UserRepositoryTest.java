package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.TypedQuery;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private final User user1 = new User();
    private final User user2 = new User();

    @Test
    void getAllEmails() {
        user1.setEmail("uuu@user.com");
        user1.setName("user");
        entityManager.persist(user1);
        user2.setEmail("uuu2@user.com");
        user2.setName("user2");
        entityManager.persist(user2);
        TypedQuery<String> query = entityManager.getEntityManager()
                .createQuery("SELECT u.email FROM User u ", String.class);
        List<String> emails = query.getResultList();
        Assertions.assertEquals(List.of("uuu2@user.com", "uuu@user.com"), emails);
    }
}