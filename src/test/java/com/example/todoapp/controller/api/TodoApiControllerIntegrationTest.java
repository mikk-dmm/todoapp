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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("default")
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
        userRepository.deleteAll();
        todoRepository.deleteAll();

        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        userRepository.save(user);

        Todo todo = new Todo();
        todo.setTitle("既存タスク");
        todo.setUser(user);
        todoRepository.save(todo);
    }

    @Test
    @WithMockUser(username = "testuser")
    void 一覧取得ができる() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    @WithMockUser(username = "testuser")
    void ID指定で取得できる() throws Exception {
        Todo todo = todoRepository.findAll().get(0);

        mockMvc.perform(get("/api/todos/" + todo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("既存タスク"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void 作成できる() throws Exception {
        String body = """
            {
                "title": "新タスク",
                "description": "説明文"
            }
        """;

        mockMvc.perform(post("/api/todos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("新タスク"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void 削除できる() throws Exception {
        Todo todo = todoRepository.findAll().get(0);

        mockMvc.perform(delete("/api/todos/" + todo.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
