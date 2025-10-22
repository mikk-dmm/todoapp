package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.form.TodoForm;
import com.example.todoapp.service.TodoService;
import com.example.todoapp.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.todoapp.security.CustomUserDetails;

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
    public String list(Model model,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("todos", todoService.findAllByCurrentUser());
        model.addAttribute("title", "Todo一覧");
        return "todo/list";
    }

    //新規作成
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
    public String createTodo(@ModelAttribute Todo todo,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        todo.setUser(userDetails.getUser());
        todoService.saveForCurrentUser(todo);
        return "redirect:/todos";
    }

    //詳細
    @GetMapping("/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id).orElseThrow(() -> new IllegalArgumentException("指定されたIDのTodoが存在しません: " + id));
        model.addAttribute("todo", todo);
        model.addAttribute("title", "Todo詳細");
        return "todo/detail";
    }

    //編集
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo todo = todoService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("指定されたIDのTodoが存在しません: " + id));

    // Todo → TodoForm に詰め替え
        TodoForm todoForm = new TodoForm();
        todoForm.setId(todo.getId());
        todoForm.setTitle(todo.getTitle());
        todoForm.setDescription(todo.getDescription());
        if (todo.getCategory() != null) {
            todoForm.setCategoryId(todo.getCategory().getId());
        }
        todoForm.setDueDate(todo.getDueDate());

        model.addAttribute("todoForm", todoForm);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "Todo編集");
        model.addAttribute("mode", "edit");
        return "todo/form";
    }


    //更新
    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable Long id, @ModelAttribute Todo todo) {
        todo.setId(id);
        todoService.saveForCurrentUser(todo);
        return "redirect:/todos";
    }

    //削除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        todoService.deleteById(id);
        return "redirect:/todos";
    }
}
