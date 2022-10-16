package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwner(Integer id);

    @Query(value = "" +
            "SELECT * " +
            "FROM items " +
            "WHERE (item_name ILIKE ?1 " +
            "OR description ILIKE ?1) " +
            "AND available = true",
            nativeQuery = true)
    List<Item> search(String text);
}
