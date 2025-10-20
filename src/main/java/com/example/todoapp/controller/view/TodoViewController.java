package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.form.TodoForm;
import com.example.todoapp.service.TodoService;
import com.example.todoapp.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/todos")
public class TodoViewController {

    private final TodoService todoService;
    private final CategoryService categoryService;

    public TodoViewController(TodoService todoService, CategoryService categoryService) {
        this.todoService = todoService;
        this.categoryService = categoryService;
    }

    //一覧表示
    @GetMapping
    public String list(Model model) {
        model.addAttribute("todos", todoService.findAll());
        model.addAttribute("title", "Todo一覧");
        return "todo/list";
    }

    //新規作成フォーム
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("todoForm", new TodoForm());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "新規Todo作成");
        model.addAttribute("mode", "new");
        return "todo/form";
    }

    //登録
    @PostMapping
    public String createTodo(@ModelAttribute Todo todo) {
        todoService.save(todo);
        return "redirect:/todos";
    }

    //編集フォーム
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id).orElseThrow(() -> new IllegalArgumentException("指定されたIDのTodoが存在しません: " + id));
        model.addAttribute("todo", todo);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "Todo編集");
        model.addAttribute("mode", "edit");
        return "todo/form";
    }

    //更新処理
    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable Long id, @ModelAttribute Todo todo) {
        todo.setId(id);
        todoService.save(todo);
        return "redirect:/todos";
    }

    //削除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        todoService.deleteById(id);
        return "redirect:/todos";
    }
}
