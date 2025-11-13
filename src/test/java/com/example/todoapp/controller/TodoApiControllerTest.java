package com.example.todoapp.controller.api;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.service.TodoService;
import com.example.todoapp.security.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@Import(TestSecurityConfig.class)
@WebMvcTest(TodoApiController.class)
class TodoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void testGetTodos() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("APIテスト");

        Page<Todo> todoPage = new PageImpl<>(List.of(todo), PageRequest.of(0, 10), 1);
        Mockito.when(todoService.findAllByCurrentUser(0, 10)).thenReturn(todoPage);

        mockMvc.perform(get("/api/todos").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("APIテスト"));
    }

    @Test
    void testFindById_Found() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("詳細");

        Mockito.when(todoService.findById(1L)).thenReturn(Optional.of(todo));

        mockMvc.perform(get("/api/todos/1").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("詳細"));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        Mockito.when(todoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/todos/999").with(user("testuser")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTodo() throws Exception {
        Todo created = new Todo();
        created.setId(1L);
        created.setTitle("Test Title");
        created.setDescription("Test Description");

        when(todoService.createTodo(any(Todo.class), any())).thenReturn(created);

        mockMvc.perform(post("/api/todos")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Title\", \"description\":\"Test Description\"}"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void testUpdateTodo() throws Exception {
        Todo existing = new Todo();
        existing.setId(1L);
        existing.setTitle("旧");

        Todo updated = new Todo();
        updated.setId(1L);
        updated.setTitle("更新後");

        Mockito.when(todoService.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(todoService.updateTodo(any(Todo.class), any())).thenReturn(updated);

        mockMvc.perform(put("/api/todos/1")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"更新後\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新後"));
    }

    @Test
    void testDeleteTodo() throws Exception {
        mockMvc.perform(delete("/api/todos/1").with(user("testuser")).with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(todoService, Mockito.times(1)).deleteById(1L);
    }
}
