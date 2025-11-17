package com.example.todoapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
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

}
