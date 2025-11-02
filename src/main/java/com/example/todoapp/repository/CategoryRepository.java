package com.example.todoapp.repository;

import com.example.todoapp.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    // ページネーション付き取得
    Page<Category> findByUserId(Long userId, Pageable pageable);

    // ページネーション付き検索
    Page<Category> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword, Pageable pageable);
}
