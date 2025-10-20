package com.example.todoapp.service;

import com.example.todoapp.entity.User;
import com.example.todoapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ユーザー一覧取得
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ユーザー取得（ID指定）
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // ユーザー登録
    public User save(User user) {
        return userRepository.save(user);
    }

    // ユーザー削除
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}