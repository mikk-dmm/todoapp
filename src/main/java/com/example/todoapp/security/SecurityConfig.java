package com.example.todoapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .csrf(csrf -> csrf.disable()) // API用途などで不要なため無効化（後で必要に応じて有効化）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/h2-console/**",
                    "/login",
                    "/register",
                    "/css/**", "/js/**", "/images/**"
                ).permitAll()
                .anyRequest().authenticated() // それ以外は認証必須
            )
            .formLogin(form -> form
                .loginPage("/login") // 独自ログインページ
                .defaultSuccessUrl("/todos", true) // ログイン成功時の遷移先
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
