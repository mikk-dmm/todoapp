package com.example.todoapp.integration;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("default")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        todoRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Todoを新規作成できること")
    void testCreateTodo() throws Exception {
        String requestBody = """
            {
                "title": "統合テストのTodo",
                "description": "統合テスト用の説明"
            }
        """;

        mockMvc.perform(post("/api/todos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("統合テストのTodo"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Todo一覧を取得できること")
    void testGetTodos() throws Exception {
        Todo todo = new Todo("統合テストTodo", "一覧テスト用", false, testUser);
        todoRepository.save(todo);

        mockMvc.perform(get("/api/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("統合テストTodo"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Todoを更新できること")
    void testUpdateTodo() throws Exception {
        Todo todo = new Todo("旧タイトル", "旧説明", false, testUser);
        todoRepository.save(todo);

        String requestBody = """
            {
                "title": "更新後タイトル",
                "description": "更新後説明"
            }
        """;

        mockMvc.perform(put("/api/todos/" + todo.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新後タイトル"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Todoを削除できること")
    void testDeleteTodo() throws Exception {
        Todo todo = new Todo("削除対象", "削除テスト用", false, testUser);
        todoRepository.save(todo);

        mockMvc.perform(delete("/api/todos/" + todo.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
