package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.User;
import com.example.todoapp.form.TodoForm;

import com.example.todoapp.service.TodoService;
import com.example.todoapp.service.CategoryService;
import com.example.todoapp.service.UserService;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/todos")
public class TodoViewController {

    private final TodoService todoService;
    private final CategoryService categoryService;
    private final UserService userService;

    public TodoViewController(TodoService todoService, CategoryService categoryService, UserService userService) {
        this.todoService = todoService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // 共通：現在ログイン中ユーザーのカテゴリを取得
    private List<Category> getUserCategories() {
        User currentUser = userService.getCurrentUser();
        return categoryService.findByUserId(currentUser.getId());
    }

    // 一覧表示
    @GetMapping
    public String listTodos(Model model) {
        List<Todo> todos = todoService.findAllByCurrentUser();
        model.addAttribute("todos", todos);
        model.addAttribute("categories", getUserCategories());
        return "todo/list";
    }

    // 新規作成フォーム表示
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        TodoForm form = new TodoForm(); // フォーム専用クラス
        model.addAttribute("todoForm", form);
        model.addAttribute("categories", getUserCategories());
        model.addAttribute("mode", "create");
        return "todo/form";
    }

    // 新規作成処理
    @PostMapping
    public String createTodo(@ModelAttribute Todo todo,
                                @RequestParam(required = false) Long categoryId) {
        todoService.createTodo(todo, categoryId);
        return "redirect:/todos";
    }

    // 編集フォーム表示
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定されたIDのTodoが存在しません:" + id));
        TodoForm todoForm = new TodoForm();
        todoForm.setId(todo.getId());
        todoForm.setTitle(todo.getTitle());
        todoForm.setDescription(todo.getDescription());
        todoForm.setDueDate(todo.getDueDate());
        todoForm.setCategoryId(
            todo.getCategory() != null ? todo.getCategory().getId() : null
        );
        model.addAttribute("todoForm", todoForm);
        model.addAttribute("categories", getUserCategories());
        model.addAttribute("mode", "edit"); // ★フォーム判定用
        return "todo/form";
    }

    //詳細フォーム
    @GetMapping("/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定されたIDのTodoが存在しません: " + id));
        model.addAttribute("todo", todo);
        model.addAttribute("title", "Todo詳細");
        return "todo/detail";
    }

    // 更新処理
    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable Long id,
                                @ModelAttribute Todo todo,
                                @RequestParam(required = false) Long categoryId) {
        todo.setId(id);
        todoService.updateTodo(todo, categoryId);
        return "redirect:/todos";
    }

    // 削除処理
    @GetMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoService.deleteById(id);
        return "redirect:/todos";
    }
}
