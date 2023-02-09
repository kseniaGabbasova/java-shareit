package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query("select i from ItemRequest i where i.requestor.id = ?1 order by i.created desc")
    List<ItemRequest> getRequestsByRequestor(Integer requestorId);

    @Query("select ir from ItemRequest ir where ir.requestor.id <> ?1")
    List<ItemRequest> getAll(Integer requestorId, Pageable pageable);
}
