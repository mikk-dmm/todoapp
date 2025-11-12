package com.example.todoapp.integration;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TodoServiceIntegrationTest {

    @Autowired
    private TodoService todoService;

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
        testUser.setUsername("service_user");
        testUser.setPassword("password");
        testUser.setEmail("service@example.com");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Todoを作成し、DBに保存されること")
    void testCreateTodo() {
        Todo todo = new Todo("ServiceTest", "サービス統合テスト", false, testUser);
        Todo saved = todoRepository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("ServiceTest");
    }

    @Test
    @DisplayName("Todoを取得できること")
    void testFindTodos() {
        Todo todo1 = new Todo("テスト1", "説明1", false, testUser);
        Todo todo2 = new Todo("テスト2", "説明2", false, testUser);
        todoRepository.saveAll(List.of(todo1, todo2));

        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).hasSize(2);
    }
}
