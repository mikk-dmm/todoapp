package com.example.todoapp.config;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                                TodoRepository todoRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            // 既にデータがある場合はスキップ
            if (userRepository.count() > 0) {
                System.out.println("Initial data already exists, skipping seeding.");
                return;
            }

            // --- ユーザー作成 ---
            User miyu = new User();
            miyu.setUsername("miyu");
            miyu.setPassword(passwordEncoder.encode("password"));
            userRepository.save(miyu);

            User alice = new User();
            alice.setUsername("alice");
            alice.setPassword(passwordEncoder.encode("password"));
            userRepository.save(alice);

            User bob = new User();
            bob.setUsername("bob");
            bob.setPassword(passwordEncoder.encode("password"));
            userRepository.save(bob);

            User charlie = new User();
            charlie.setUsername("charlie");
            charlie.setPassword(passwordEncoder.encode("password"));
            userRepository.save(charlie);

            // --- 各ユーザーに10件ずつTodo作成 ---
            for (int i = 1; i <= 10; i++) {
                todoRepository.save(new Todo("Miyu Todo " + i, "みちゃのタスク" + i + "です", false, miyu));
                todoRepository.save(new Todo("Alice Todo " + i, "Aliceのタスク" + i + "です", false, alice));
                todoRepository.save(new Todo("Bob Todo " + i, "Bobのタスク" + i + "です", i == 1, bob));
                todoRepository.save(new Todo("Charlie Todo " + i, "Charlieのタスク" + i + "です", i == 1, charlie));
            }

            System.out.println("✅ Initial data inserted successfully!");
        };
    }
}
