package com.example.todoapp.controller.view;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.service.CategoryService;
import com.example.todoapp.service.TodoService;
import com.example.todoapp.service.UserService;
import com.example.todoapp.security.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@Import(TestSecurityConfig.class)
@WebMvcTest(TodoViewController.class)
class TodoViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        Mockito.when(userService.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(categoryService.findByUserId(1L)).thenReturn(List.of(new Category(1L, "仕事", mockUser)));
    }

    @Test
    void testListTodos() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("テストタスク");
        Page<Todo> todoPage = new PageImpl<>(List.of(todo), PageRequest.of(0, 5), 1);

        Mockito.when(todoService.searchTodosWithSort(anyString(), anyString(), any())).thenReturn(todoPage);

        mockMvc.perform(get("/todos").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/list"))
                .andExpect(model().attributeExists("todoPage", "todos", "categories"));
    }

    @Test
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/todos/new").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/form"))
                .andExpect(model().attributeExists("todoForm", "categories", "mode"));
    }

    @Test
    void testShowDetail() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("詳細テスト");

        Mockito.when(todoService.findById(1L)).thenReturn(Optional.of(todo));

        mockMvc.perform(get("/todos/1").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/detail"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    void testCreateTodo() throws Exception {
        mockMvc.perform(post("/todos")
                        .param("title", "新しいタスク")
                        .with(user("testuser"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        Mockito.verify(todoService, Mockito.times(1)).createTodo(any(Todo.class), any());
    }
}
