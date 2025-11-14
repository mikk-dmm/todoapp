package com.example.todoapp.dto.todo;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import org.springframework.stereotype.Component;

/**
 * Converts between Todo entities and DTOs.
 */
@Component
public class TodoMapper {

    public Todo toEntity(TodoRequest request) {
        Todo todo = new Todo();
        applyRequest(todo, request);
        return todo;
    }

    public void applyRequest(Todo todo, TodoRequest request) {
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setStatus(request.getStatus());
        todo.setDueDate(request.getDueDate());
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
    }

    public TodoResponse toResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setStatus(todo.getStatus());
        response.setDueDate(todo.getDueDate());
        response.setCompleted(todo.isCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        response.setDeadlineStatus(todo.getDeadlineStatus());

        Category category = todo.getCategory();
        if (category != null) {
            response.setCategoryId(category.getId());
            response.setCategoryName(category.getName());
        }

        User user = todo.getUser();
        if (user != null) {
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
        }

        return response;
    }
}
