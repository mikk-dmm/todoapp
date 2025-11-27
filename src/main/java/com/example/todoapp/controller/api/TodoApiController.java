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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

    public TodoApiController(TodoService todoService, TodoMapper todoMapper) {
        this.todoService = todoService;
        this.todoMapper = todoMapper;
    }


    // 一覧取得
    @Operation(
        summary = "Todo一覧取得",
        description = "ログイン中のユーザーに紐づくTodo一覧をページング形式で取得します。ページ番号とページサイズを指定できます。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "正常に取得しました"),
        @ApiResponse(responseCode = "401", description = "認証エラー（ログインが必要です）")
    })
    @GetMapping
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TodoResponse> todos = todoService.findAllByCurrentUser(page, size)
                .map(todoMapper::toResponse);
        return ResponseEntity.ok(todos);
    }

    // 詳細取得
    @Operation(
        summary = "Todo詳細取得",
        description = "指定したIDのTodoを1件取得します。ログインユーザーに紐づくTodoのみ取得可能です。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "正常に取得しました"),
        @ApiResponse(responseCode = "404", description = "指定されたIDのTodoが存在しません")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> findById(@Parameter(description = "Todoを紐付けるカテゴリID") @PathVariable Long id) {
        return todoService.findByIdForCurrentUser(id)
                .map(todoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新規作成
    @Operation(
        summary = "Todo新規作成",
        description = "新しいTodoを作成します。titleは必須です。作成されたTodoはログインユーザーに紐づきます。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Todoを作成しました"),
        @ApiResponse(responseCode = "400", description = "リクエスト内容が不正です")
    })
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        try {
            Todo todo = todoMapper.toEntity(request);
            Todo created = todoService.createTodo(todo, request.getCategoryId());
            return ResponseEntity.status(HttpStatus.CREATED).body(todoMapper.toResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 更新
    @Operation(
        summary = "Todo更新",
        description = "指定したTodoを更新します。title、 description、カテゴリIDを含むフィールドを編集可能です。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Todoを更新しました"),
        @ApiResponse(responseCode = "404", description = "指定されたIDのTodoが存在しません")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@Parameter(description ="Todoを紐づけるカテゴリID") @PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        try {
            return todoService.findByIdForCurrentUser(id)
                    .map(existing -> {
                        todoMapper.applyRequest(existing, request);
                        Todo updated = todoService.updateTodo(existing, request.getCategoryId());
                        return ResponseEntity.ok(todoMapper.toResponse(updated));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 削除
    @Operation(
        summary = "Todo削除",
        description = "指定したIDのTodoを削除します。ログインユーザー自身のTodoのみ削除可能です。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "削除成功"),
        @ApiResponse(responseCode = "404", description = "指定されたIDのTodoが存在しません")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            todoService.deleteById(id);
            return ResponseEntity.noContent().build();
            } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
