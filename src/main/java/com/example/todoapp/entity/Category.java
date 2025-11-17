package com.example.todoapp.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Todoとの1対多リレーション
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos;

    // Userとの多対1リレーション
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Category() {}
    public Category(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

}
