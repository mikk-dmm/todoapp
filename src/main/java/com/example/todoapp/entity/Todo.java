package com.example.todoapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.beans.Transient;
import java.lang.annotation.Inherited;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "タイトルは必須です")
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean completed = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = Status.TODO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = Status.TODO;
        }
    }

    @Transient
    public String getDeadlineStatus() {
        if (dueDate == null) return "期限なし";

        long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

        if (daysBetween > 1) {
            return "あと" + daysBetween + "日";
        } else if (daysBetween == 1) {
            return "1日前";
        } else if (daysBetween == 0) {
            return "本日期限";
        } else {
            return "期限切れ";
        }
    }

    public String getStatusClass() {
        if (dueDate == null) return "";
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, dueDate);

        if (daysBetween <= 1) {
            return "text-red-600 font-semibold";
        }
        return "";
    }

    public Todo() {}

    public Todo(String title, String description, boolean completed, User user) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed;}
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
