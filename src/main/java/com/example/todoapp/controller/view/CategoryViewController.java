package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Category;
import com.example.todoapp.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService categoryService;

    public CategoryViewController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 検索・一覧 + ページネーション
    @GetMapping
    public String listCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        String sanitizedKeyword = (StringUtils.hasText(keyword) && !"null".equalsIgnoreCase(keyword.trim())) ? keyword.trim() : "";
        Page<Category> categoryPage = categoryService.search(sanitizedKeyword, page, size);

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("keyword", sanitizedKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("title", "カテゴリ一覧");

        return "category/list";
    }

    // 新規フォーム表示
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("title", "新規カテゴリ作成");
        return "category/form";
    }

    // 編集
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findByIdForCurrentUser(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        model.addAttribute("category", category);
        model.addAttribute("title", "カテゴリ編集");
        return "category/form";
    }

    // 新規作成
    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    // 更新
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category) {
        categoryService.update(id, category);
        return "redirect:/categories";
    }

    // 削除
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
