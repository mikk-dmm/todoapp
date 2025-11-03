package com.example.todoapp.form;

import java.time.LocalDate;
import com.example.todoapp.entity.*;

public class TodoForm {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private LocalDate dueDate;
    private Status status;

    // --- getter & setter ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
