package com.example.todoapp.service;

import com.example.todoapp.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    // ログインユーザーの一覧取得
    List<Category> findAllForCurrentUser();

    // ログインユーザーのID取得
    Optional<Category> findByIdForCurrentUser(Long id);

    // 新規登録
    Category save(Category category);

    // ログインユーザーのみ自分のカテゴリ更新
    Category update(Long id, Category category);

    // ログインユーザーのみ自分のカテゴリ削除
    void delete(Long id);

    // カテゴリ検索
    Page<Category> search(String keyword, int page, int size);
}
