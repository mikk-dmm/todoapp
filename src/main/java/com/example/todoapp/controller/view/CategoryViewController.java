package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.User;
import com.example.todoapp.service.CategoryService;
import com.example.todoapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryViewController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // ✅ 検索＋一覧表示を統一
    @GetMapping
    public String listCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        User currentUser = userService.getCurrentUser();
        List<Category> categories;

        if (keyword == null || keyword.trim().isEmpty()) {
            categories = categoryService.findByUserId(currentUser.getId());
        } else {
            categories = categoryService.searchCategories(keyword);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", "カテゴリ一覧");

        return "category/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("title", "新規カテゴリ作成");
        return "category/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        model.addAttribute("category", category);
        model.addAttribute("title", "カテゴリ編集");
        return "category/form";
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category) {
        categoryService.update(id, category);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
