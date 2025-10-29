package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Category;
import com.example.todoapp.service.CategoryService;
import com.example.todoapp.service.UserService;
import com.example.todoapp.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryViewController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // 一覧表示
    @GetMapping
    public String list(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("categories", categoryService.findByUserId(currentUser.getId()));
        model.addAttribute("title", "カテゴリ一覧");
        return "category/list";
    }

    // 新規作成フォーム
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("title", "新規カテゴリ作成");
        return "category/form";
    }

    // 編集フォーム
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        model.addAttribute("category", category);
        model.addAttribute("title", "カテゴリ編集");
        return "category/form";
    }

    // 登録処理（新規作成）
    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        User currentUser = userService.getCurrentUser();
        category.setUser(currentUser);
        categoryService.save(category);
        return "redirect:/categories";
    }

    // 更新処理
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category) {
        User currentUser = userService.getCurrentUser();
        categoryService.update(id, category);
        return "redirect:/categories";
    }

    // 削除処理
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
