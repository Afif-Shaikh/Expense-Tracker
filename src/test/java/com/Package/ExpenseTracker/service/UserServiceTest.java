package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.UserService;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser() {
        User user = userService.register("Test User", "test@test.com", "password123");

        assertNotNull(user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@test.com", user.getEmail());
        // Password should be encoded, not plain text
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    void shouldNotRegisterDuplicateEmail() {
        userService.register("User One", "same@test.com", "password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register("User Two", "same@test.com", "password456");
        });

        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        userService.register("Test User", "login@test.com", "password123");

        User user = userService.login("login@test.com", "password123");

        assertNotNull(user);
        assertEquals("login@test.com", user.getEmail());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        userService.register("Test User", "wrong@test.com", "password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("wrong@test.com", "wrongpassword");
        });

        assertTrue(exception.getMessage().contains("Invalid"));
    }

    @Test
    void shouldFailLoginWithNonExistentEmail() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("nonexistent@test.com", "password123");
        });

        assertTrue(exception.getMessage().contains("Invalid"));
    }

    @Test
    void shouldFindUserByEmail() {
        userService.register("Find Me", "findme@test.com", "password123");

        User found = userService.findByEmail("findme@test.com");

        assertEquals("Find Me", found.getName());
    }
}