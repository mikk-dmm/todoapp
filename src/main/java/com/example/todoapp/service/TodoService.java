package com.example.todoapp.service;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.entity.Status;
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
    private final CurrentUserProvider currentUserProvider;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, CurrentUserProvider currentUserProvider, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.currentUserProvider = currentUserProvider;
        this.categoryRepository = categoryRepository;
    }

    //認証ユーザー取得
    private User getCurrentUser() {
        return currentUserProvider.getCurrentUser();
    }

    //認証ユーザーID取得
    private Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    //所有者チェック+例外専用（更新、削除）
    private Todo loadOwnedTodo(Long id) {
        return todoRepository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Todoはありません: " + id));
    }

    // Todo作成
    public Todo createTodo(Todo todo, Long categoryId) {
        User currentUser = getCurrentUser();
        todo.setUser(currentUser);
        applyDefaultStatus(todo);
        validateAndAssignCategory(todo, categoryId, currentUser);
        return todoRepository.save(todo);
    }

    // Todo更新
    public Todo updateTodo(Todo todo, Long categoryId) {
        if (todo.getId() == null) {
            throw new IllegalArgumentException("更新にはTodoidが必要です");
        }
        Todo existing = loadOwnedTodo(todo.getId());
        existing.setTitle(todo.getTitle());
        existing.setDescription(todo.getDescription());
        existing.setDueDate(todo.getDueDate());
        existing.setStatus(todo.getStatus());
        existing.setCompleted(todo.isCompleted());
        applyDefaultStatus(existing);
        validateAndAssignCategory(existing, categoryId, getCurrentUser());
        return todoRepository.save(existing);
    }

    //ページネーション対応一覧
    public Page<Todo> findAllByCurrentUser(int page, int size) {
        User currentUser = currentUserProvider.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return todoRepository.findByUser(currentUser, pageable);
    }

    //ページネーション対応検索
    public Page<Todo> searchTodos(String keyword, int page, int size) {
        User currentUser = currentUserProvider.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.isEmpty()) {
            return todoRepository.findByUser(currentUser, pageable);
        } else {
            return todoRepository.findByUserAndTitleContainingIgnoreCase(currentUser, keyword, pageable);
        }
    }

    //管理者用
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    //所有者チェック（詳細、編集）
    public Optional<Todo> findByIdForCurrentUser(Long id) {
        return todoRepository.findByIdAndUserId(id, getCurrentUserId());
    }

    public void deleteById(Long id) {
        Todo todo = loadOwnedTodo(id);
        todoRepository.delete(todo);
    }

    //ソート機能対応検索
    public Page<Todo> searchTodosWithSort(String keyword, String sort, Pageable pageable) {
        User currentUser = currentUserProvider.getCurrentUser();

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
                sortOption = Sort.by(Sort.Direction.ASC, "dueDate");
                break;
            case "created_at_desc":
                sortOption = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
            case "created_at_asc":
                sortOption = Sort.by(Sort.Direction.ASC, "createdAt");
                break;
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

    public void toggleCompleted(Long id) {
        Todo todo = loadOwnedTodo(id);
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
    }

    private void applyDefaultStatus(Todo todo) {
        if (todo.getStatus() == null) {
            todo.setStatus(Status.TODO);
        }
    }

    private void validateAndAssignCategory(Todo todo, Long categoryId, User currentUser) {
        if (categoryId == null) {
            todo.setCategory(null);
            return;
        }
        Category category = categoryRepository.findByIdAndUserId(categoryId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to current user: " + categoryId));
        todo.setCategory(category);
    }
}
