package com.example.todoapp.repository;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.category WHERE t.user = :user")
    List<Todo> findByUser(User user);
    List<Todo> findByUserAndTitleContainingIgnoreCase(User user, String keyword);

    @EntityGraph(attributePaths = {"category"})
    Page<Todo> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Todo> findByUserAndTitleContainingIgnoreCase(User user, String keyword, Pageable pageable);

    List<Todo> findByTitleContainingIgnoreCase(String keyword);

}
