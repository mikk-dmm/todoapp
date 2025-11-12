package com.example.todoapp.controller.api;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Spring Boot起動 + MockMvc利用
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // テストユーザー作成
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        userRepository.save(user);

        // Todo1件登録
        Todo todo = new Todo();
        todo.setTitle("既存タスク");
        todo.setUser(user);
        todoRepository.save(todo);
    }

    @Test
    void 一覧取得ができる() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    void ID指定で取得できる() throws Exception {
        Todo todo = todoRepository.findAll().get(0);

        mockMvc.perform(get("/api/todos/" + todo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("既存タスク"));
    }

    @Test
    void Todoを新規登録できる() throws Exception {
        String json = """
                {
                    "title": "新しいタスク",
                    "description": "説明テキスト"
                }
                """;

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("新しいタスク"));
    }

    @Test
    void Todoを更新できる() throws Exception {
        Todo todo = todoRepository.findAll().get(0);
        String json = """
                {
                    "title": "更新後タイトル",
                    "description": "更新後説明"
                }
                """;

        mockMvc.perform(put("/api/todos/" + todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新後タイトル"));
    }

    @Test
    void Todoを削除できる() throws Exception {
        Todo todo = todoRepository.findAll().get(0);

        mockMvc.perform(delete("/api/todos/" + todo.getId()))
                .andExpect(status().isNoContent());
    }
}
