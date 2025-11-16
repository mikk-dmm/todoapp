package com.example.todoapp.controller.api;

import com.example.todoapp.entity.Category;
import com.example.todoapp.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 一覧取得
    @Operation(
        summary = "カテゴリ一覧取得",
        description = "全カテゴリを取得します。ユーザー共通のカテゴリとして扱われます。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "正常に取得しました")
    })
    @GetMapping
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    // ID指定取得
    @Operation(
        summary = "カテゴリ詳細取得",
        description = "指定されたカテゴリIDの情報を取得します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "正常に取得しました"),
        @ApiResponse(responseCode = "404", description = "カテゴリが存在しません")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新規登録
    @Operation(
        summary = "カテゴリ新規登録",
        description = "新しいカテゴリを作成します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "作成しました")
    })
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.save(category);
    }

    // 更新
    @Operation(
        summary = "カテゴリ更新",
        description = "カテゴリ名などの情報を更新します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新しました"),
        @ApiResponse(responseCode = "404", description = "カテゴリが存在しません")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updated = categoryService.update(id, category);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 削除
    @Operation(
        summary = "カテゴリ削除",
        description = "指定されたカテゴリを削除します。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "削除成功")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}