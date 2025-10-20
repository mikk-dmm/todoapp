package com.example.todoapp.service;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
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
}
