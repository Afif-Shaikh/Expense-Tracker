package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.repository.UserRepository;
import com.Package.ExpenseTracker.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void cleanup() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Test User",
                "email", "test@test.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void shouldFailRegisterWithDuplicateEmail() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "User One",
                "email", "dup@test.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldFailRegisterWithInvalidEmail() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Test",
                "email", "not-an-email",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailRegisterWithShortPassword() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Test",
                "email", "test@test.com",
                "password", "123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Register first
        String registerBody = objectMapper.writeValueAsString(Map.of(
                "name", "Test User",
                "email", "login@test.com",
                "password", "password123"
        ));
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody));

        // Login
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "login@test.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        // Register
        String registerBody = objectMapper.writeValueAsString(Map.of(
                "name", "Test",
                "email", "fail@test.com",
                "password", "password123"
        ));
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody));

        // Wrong password
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "fail@test.com",
                "password", "wrongpassword"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized());
    }
}