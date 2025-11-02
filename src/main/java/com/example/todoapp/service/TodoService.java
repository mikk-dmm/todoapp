package com.example.todoapp.service;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.CategoryRepository;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, UserService userService, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    // Todo作成
    public Todo createTodo(Todo todo, Long categoryId) {
        User currentUser = userService.getCurrentUser();
        todo.setUser(currentUser);
        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(todo::setCategory);
        }
        return todoRepository.save(todo);
    }

    // Todo更新
    public Todo updateTodo(Todo todo, Long categoryId) {
        User currentUser = userService.getCurrentUser();
        todo.setUser(currentUser);
        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(todo::setCategory);
        } else {
            todo.setCategory(null);
        }
        return todoRepository.save(todo);
    }

    // 一覧（ログインユーザー限定）
    public List<Todo> findAllByCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return todoRepository.findByUser(currentUser);
    }

    // 検索（ログインユーザー限定）
    public List<Todo> searchTodos(String keyword) {
        User currentUser = userService.getCurrentUser();
        if (keyword == null || keyword.isEmpty()) {
            return todoRepository.findByUser(currentUser);
        } else {
            return todoRepository.findByUserAndTitleContainingIgnoreCase(currentUser, keyword);
        }
    }

    // 単体取得
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    // 削除
    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }
}
