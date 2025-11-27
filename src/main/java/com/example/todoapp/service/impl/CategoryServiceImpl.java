package com.example.todoapp.service.impl;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.CategoryRepository;
import com.example.todoapp.service.CategoryService;
import com.example.todoapp.service.CurrentUserProvider;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CurrentUserProvider currentUserProvider;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CurrentUserProvider currentUserProvider) {
        this.categoryRepository = categoryRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // 現在ログイン中のユーザーを取得（共通ヘルパー）
    private User getCurrentUser() {
        return currentUserProvider.getCurrentUser();
    }

    private Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    // 指定IDのカテゴリが currentUser 所有であることを検証し取得する
    private Category loadOwnedCategory(Long id) {
        return categoryRepository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found for current user: " + id));
    }

    // ログイン中ユーザーのカテゴリ一覧を取得
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllForCurrentUser() {
        return categoryRepository.findByUserId(getCurrentUserId());
    }

    // ID とユーザーを基準にカテゴリを取得（所有者チェック付き）
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByIdForCurrentUser(Long id) {
        return categoryRepository.findByIdAndUserId(id, getCurrentUserId());
    }

    // 新規カテゴリを currentUser に紐づけて保存
    @Override
    public Category save(Category category) {
        category.setUser(getCurrentUser());
        return categoryRepository.save(category);
    }

    // 所有者チェック後、カテゴリ名を削除
    @Override
    public void delete(Long id) {
        Category category = loadOwnedCategory(id);
        categoryRepository.delete(category);
    }

    // 所有者チェック後、カテゴリを更新
    @Override
    public Category update(Long id, Category category) {
        Category existing = loadOwnedCategory(id);
        existing.setName(category.getName());
        return categoryRepository.save(existing);
    }

    // ユーザー別カテゴリの検索＋ページネーション
    @Override
    @Transactional(readOnly = true)
    public Page<Category> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = getCurrentUserId();
        if (!StringUtils.hasText(keyword) || "null".equalsIgnoreCase(keyword.trim())) {
            return categoryRepository.findByUserId(userId, pageable);
        }
        String sanitizedKeyword = keyword.trim();
        return categoryRepository.findByUserIdAndNameContainingIgnoreCase(userId, sanitizedKeyword, pageable);
    }

}
