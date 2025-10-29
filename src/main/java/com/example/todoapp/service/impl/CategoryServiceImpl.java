package com.example.todoapp.service.impl;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.CategoryRepository;
import com.example.todoapp.service.CategoryService;
import com.example.todoapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        //現在のログインユーザー
        User currentUser = userService.getCurrentUser();
        category.setUser(currentUser);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Category update(Long id, Category category) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        existing.setName(category.getName());
        //Userの更新は行わないが、nullの場合は現在のユーザーをセットまたが更新
        if (existing.getUser() == null) {
            existing.setUser(userService.getCurrentUser());
        }

        return categoryRepository.save(existing);
    }
}
