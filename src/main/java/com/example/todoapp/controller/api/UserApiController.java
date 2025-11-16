package com.example.todoapp.controller.api;

import com.example.todoapp.entity.User;
import com.example.todoapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    // 一覧取得
    @Operation(
        summary = "ユーザー一覧取得",
        description = "登録されている全ユーザーを取得します。（管理者用API)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "正常に取得しました"),
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // ID指定取得
    @Operation(
        summary = "ユーザー詳細取得",
        description = "指定されたIDのユーザー情報を取得します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "正常に取得しました"),
        @ApiResponse(responseCode = "404", description = "指定されたユーザーが存在しません")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@Parameter(description = "取得したいID") @PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新規登録（JSONで登録）
    @Operation(
        summary = "ユーザー登録",
        description = "ユーザーを新規登録します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登録完了"),
        @ApiResponse(responseCode = "400", description = "不正なリクエスト")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // 更新
    @Operation(
        summary = "ユーザー更新",
        description = "指定されたIDのユーザー情報を更新します。username/password/roleなどを変更できます。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新しました"),
        @ApiResponse(responseCode = "404", description = "指定されたユーザーが存在しません")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@Parameter(description = "更新するID") @PathVariable Long id, @RequestBody User userDetails) {
        return userService.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setPassword(userDetails.getPassword());
                    user.setRole(userDetails.getRole());
                    return ResponseEntity.ok(userService.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 削除
    @Operation(
        summary = "ユーザー削除",
        description = "指定したユーザーを削除します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "削除しました"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description ="削除するID") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
