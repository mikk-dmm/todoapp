package com.example.todoapp.service;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.CategoryRepository;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    //ページネーション対応一覧
    public Page<Todo> findAllByCurrentUser(int page, int size) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return todoRepository.findByUser(currentUser, pageable);
    }

    //ページネーション対応検索
    public Page<Todo> searchTodos(String keyword, int page, int size) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.isEmpty()) {
            return todoRepository.findByUser(currentUser, pageable);
        } else {
            return todoRepository.findByUserAndTitleContainingIgnoreCase(currentUser, keyword, pageable);
        }
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }

    public Page<Todo> searchTodosWithPagination(String keyword, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        if (keyword == null || keyword.isEmpty()) {
            return todoRepository.findByUser(currentUser, pageable);
        } else {
            return todoRepository.findByUserAndTitleContainingIgnoreCase(currentUser, keyword, pageable);
        }
    }

    public Page<Todo> searchTodosWithSort(String keyword, String sort, Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        Sort sortOption;
        switch (sort) {
            case "title_asc":
                sortOption = Sort.by(Sort.Direction.ASC, "title");
                break;
            case "title_desc":
                sortOption = Sort.by(Sort.Direction.DESC, "title");
                break;
            case "dueDate_desc":
                sortOption = Sort.by(Sort.Direction.DESC, "dueDate");
                break;
            case "dueDate_asc":
            default:
                sortOption = Sort.by(Sort.Direction.ASC, "dueDate");
                break;
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

        if (keyword == null || keyword.isEmpty()) {
            return todoRepository.findByUser(currentUser, sortedPageable);
        } else {
            return todoRepository.findByUserAndTitleContainingIgnoreCase(currentUser, keyword, sortedPageable);
        }
    }
}
