package com.example.todoapp.controller.api;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<Page<Todo>> getTodos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Page<Todo> todos = todoService.findAllByCurrentUser(page, size);
        return ResponseEntity.ok(todos);
    }


    // ID指定で取得
    @GetMapping("/{id}")
    public ResponseEntity<Todo> findById(@PathVariable Long id) {
        return todoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新規登録
    @PostMapping
    public Todo createTodo(@RequestBody Map<String, Object> payload) {
        // JSONからTodo本体を作成
        Todo todo = new Todo();
        todo.setTitle((String) payload.get("title"));
        todo.setDescription((String) payload.get("description"));

        // categoryIdを取得（存在しない場合はnull）
        Long categoryId = payload.get("categoryId") != null
                ? ((Number) payload.get("categoryId")).longValue()
                : null;

        return todoService.createTodo(todo, categoryId);
    }

    /*更新*/
    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        if (todoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle((String) payload.get("title"));
        todo.setDescription((String) payload.get("description"));

        Long categoryId = payload.get("categoryId") != null
                ? ((Number) payload.get("categoryId")).longValue()
                : null;

        Todo updated = todoService.updateTodo(todo, categoryId);
        return ResponseEntity.ok(updated);
    }

    // 削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
