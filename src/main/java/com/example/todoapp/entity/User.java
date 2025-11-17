package com.example.todoapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    private String email;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Category> categories = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role != null ? role : "ROLE_USER";
        this.email = email;
    }

    public void setRole(String role) {
        if (role == null || role.isBlank()) {
            this.role = "ROLE_USER";
        } else {
            this.role = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }
    }
}
