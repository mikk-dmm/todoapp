package com.example.todoapp.service;

import com.example.todoapp.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void delete(Long id);
    List<Category> findByUserId(Long userId);
    Category update(Long id, Category category);
    List<Category> searchCategories(String keyword);
}
