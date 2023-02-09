package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "email")
    private String email;
}