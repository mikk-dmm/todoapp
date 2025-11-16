package com.example.todoapp.service;

import com.example.todoapp.entity.User;

/**
 * Abstraction used by application services to access the authenticated user.
 * <p>
 * Isolated behind an interface so that tests can provide lightweight
 * implementations without relying on Mockito/ByteBuddy instrumentation.
 */
@FunctionalInterface
public interface CurrentUserProvider {

    User getCurrentUser();
}
