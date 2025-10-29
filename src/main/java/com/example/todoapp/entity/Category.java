package com.example.todoapp.entity;

import jakarta.persistence.*;

import java.lang.annotation.Inherited;
import java.util.List;

@Entity
@Table(name = "categories")
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Todo> getTodos() { return todos; }
    public void setTodos(List<Todo> todos) { this.todos = todos; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}