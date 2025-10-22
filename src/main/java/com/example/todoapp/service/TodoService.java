package com.example.todoapp.service;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.todoapp.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    // Todo作成
    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    // Todo一覧取得
    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    // Todo取得（ID指定）
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    // Todo保存
    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    // Todo削除
    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }

    public List<Todo> findAllByCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return todoRepository.findByUser(user);
    }

    public Todo saveForCurrentUser(Todo todo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        todo.setUser(user);
        return todoRepository.save(todo);
    }
}
