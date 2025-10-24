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

    // Todo作成（ログイン中ユーザー カテゴリを紐づけ）
    public Todo createTodo(Todo todo, Long categoryId) {
        User currentUser = userService.getCurrentUser();
        todo.setUser(currentUser);

        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(todo::setCategory);
        }

        return todoRepository.save(todo);
    }

    // Todo更新（ログイン中ユーザー カテゴリを再紐づけ）
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

    // Todo一覧（ログイン中ユーザーのみ）
    public List<Todo> findAllByCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return todoRepository.findByUser(currentUser);
    }

    // Todo取得（ID指定）
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    // Todo削除
    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }
}
