package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "" +
            "SELECT email " +
            "FROM users",
            nativeQuery = true)
    List<String> getAllEmails();


}
