package com.example.todoapp.service;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.CategoryRepository;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TodoService todoService;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setTitle("Test Todo");
        testTodo.setCompleted(false);
        testTodo.setUser(testUser);
    }

    @Test
    void testCreateTodo_Success() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo result = todoService.createTodo(testTodo, null);

        assertThat(result.getTitle()).isEqualTo("Test Todo");
        assertThat(result.getUser()).isEqualTo(testUser);

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testFindAllByCurrentUser_ReturnsPage() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        Page<Todo> mockPage = new PageImpl<>(Collections.singletonList(testTodo));
        when(todoRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(mockPage);

        Page<Todo> result = todoService.findAllByCurrentUser(0, 5);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Todo");
        verify(todoRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testToggleCompleted_TogglesAndSaves() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        testTodo.setCompleted(false);

        todoService.toggleCompleted(1L);

        assertThat(testTodo.isCompleted()).isTrue();
        verify(todoRepository).save(testTodo);
    }

    @Test
    void testToggleCompleted_ThrowsExceptionIfNotFound() {
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> todoService.toggleCompleted(999L));
    }
}
