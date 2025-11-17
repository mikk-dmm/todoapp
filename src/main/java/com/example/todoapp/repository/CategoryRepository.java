package com.example.todoapp.repository;

import com.example.todoapp.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 紐付けユーザー取得
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    // ユーザー別カテゴリ一覧
    List<Category> findByUserId(Long userId);

    // ページネーション付き取得
    Page<Category> findByUserId(Long userId, Pageable pageable);

    // ページネーション付き検索
    Page<Category> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword, Pageable pageable);
}
