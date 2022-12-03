package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Getter
@EqualsAndHashCode
@Setter
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;
    @NotBlank
    @Column(name = "item_name")
    private String name;
    @NotBlank
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @Column(name = "owner_id")
    private Integer owner;
    @Column(name = "request_id")
    private Integer requestId;
}
