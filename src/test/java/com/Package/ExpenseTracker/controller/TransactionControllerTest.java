package com.Package.ExpenseTracker.controller;

import com.Package.ExpenseTracker.repository.TransactionRepository;
import com.Package.ExpenseTracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private MockHttpSession session;

    @BeforeEach
    void setup() throws Exception {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        // Register user
        String registerBody = objectMapper.writeValueAsString(Map.of(
                "name", "Test User",
                "email", "test@test.com",
                "password", "password123"
        ));
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody));

        // Login and capture session
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "test@test.com",
                "password", "password123"
        ));
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession();
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Groceries",
                "amount", 500.00,
                "date", "2026-03-20",
                "category", "Food",
                "type", "EXPENSE"
        ));

        mockMvc.perform(post("/api/transactions")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    void shouldRejectInvalidTransaction() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "",
                "amount", -100,
                "date", "2030-12-31",
                "category", "Food",
                "type", "INVALID"
        ));

        mockMvc.perform(post("/api/transactions")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllTransactions() throws Exception {
        createTransaction("Salary", 50000, "INCOME");
        createTransaction("Groceries", 500, "EXPENSE");

        mockMvc.perform(get("/api/transactions")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetTransactionsByType() throws Exception {
        createTransaction("Salary", 50000, "INCOME");
        createTransaction("Freelance", 10000, "INCOME");
        createTransaction("Groceries", 500, "EXPENSE");

        mockMvc.perform(get("/api/transactions/type/INCOME")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetSummary() throws Exception {
        createTransaction("Salary", 50000, "INCOME");
        createTransaction("Groceries", 500, "EXPENSE");

        mockMvc.perform(get("/api/transactions/summary")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(50000.00))
                .andExpect(jsonPath("$.totalExpense").value(500.00))
                .andExpect(jsonPath("$.balance").value(49500.00));
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        createTransaction("Groceries", 500, "EXPENSE");

        // Get the created transaction's ID
        MvcResult getResult = mockMvc.perform(get("/api/transactions")
                        .session(session))
                .andReturn();

        String response = getResult.getResponse().getContentAsString();
        int id = objectMapper.readTree(response).get(0).get("id").asInt();

        String updateBody = objectMapper.writeValueAsString(Map.of(
                "name", "Updated Groceries",
                "amount", 750.00,
                "date", "2026-03-20",
                "category", "Food",
                "type", "EXPENSE"
        ));

        mockMvc.perform(put("/api/transactions/" + id)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Groceries"))
                .andExpect(jsonPath("$.amount").value(750.00));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        createTransaction("Groceries", 500, "EXPENSE");

        MvcResult getResult = mockMvc.perform(get("/api/transactions")
                        .session(session))
                .andReturn();

        String response = getResult.getResponse().getContentAsString();
        int id = objectMapper.readTree(response).get(0).get("id").asInt();

        mockMvc.perform(delete("/api/transactions/" + id)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));

        // Verify deleted
        mockMvc.perform(get("/api/transactions")
                        .session(session))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn404ForNonExistentTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/99999")
                        .session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());
    }

    // Helper
    private void createTransaction(String name, double amount, String type) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "name", name,
                "amount", amount,
                "date", "2026-03-20",
                "category", "Food",
                "type", type
        ));

        mockMvc.perform(post("/api/transactions")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}