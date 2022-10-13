package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;
    @Column(name = "comment_text")
    private String text;

    @Column(name = "item_id")
    private Integer item;

    @Column(name = "author_id")
    private Integer author;
    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
}