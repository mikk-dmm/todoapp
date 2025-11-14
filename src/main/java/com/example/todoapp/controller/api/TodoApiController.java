package com.example.todoapp.controller.api;

import com.example.todoapp.dto.todo.TodoMapper;
import com.example.todoapp.dto.todo.TodoRequest;
import com.example.todoapp.dto.todo.TodoResponse;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

    public TodoApiController(TodoService todoService, TodoMapper todoMapper) {
        this.todoService = todoService;
        this.todoMapper = todoMapper;
    }

    @GetMapping
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TodoResponse> todos = todoService.findAllByCurrentUser(page, size)
                .map(todoMapper::toResponse);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> findById(@PathVariable Long id) {
        return todoService.findById(id)
                .map(todoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        Todo todo = todoMapper.toEntity(request);
        Todo created = todoService.createTodo(todo, request.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(todoMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return todoService.findById(id)
                .map(existing -> {
                    todoMapper.applyRequest(existing, request);
                    Todo updated = todoService.updateTodo(existing, request.getCategoryId());
                    return ResponseEntity.ok(todoMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
