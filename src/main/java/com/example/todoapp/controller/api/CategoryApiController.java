package com.example.todoapp.controller.api;

import com.example.todoapp.entity.Category;
import com.example.todoapp.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //一覧取得
    @GetMapping
    public List<Category> findAll() {
        return categoryService.findAll();
    }


    //カテゴリ取得
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //登録
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.save(category);
    }


    //削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
