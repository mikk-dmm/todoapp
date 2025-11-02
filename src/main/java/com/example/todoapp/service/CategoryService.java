package com.example.todoapp.service;

import com.example.todoapp.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void delete(Long id);
    List<Category> findByUserId(Long userId);
    Category update(Long id, Category category);
    Page<Category> searchCategories(String keyword, int page, int size);
}
