package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.TransactionService;
import com.Package.ExpenseTracker.exception.TransactionNotFoundException;
import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.TransactionRepository;
import com.Package.ExpenseTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("Test User", "test@test.com", "encoded_password");
        testUser = userRepository.save(testUser);

        otherUser = new User("Other User", "other@test.com", "encoded_password");
        otherUser = userRepository.save(otherUser);
    }

    @Test
    void shouldCreateTransaction() {
        Transaction t = new Transaction(
                "Groceries", new BigDecimal("500.00"),
                LocalDate.now(), "Food", "EXPENSE", "Weekly groceries"
        );

        Transaction saved = transactionService.create(t, testUser);

        assertNotNull(saved.getId());
        assertEquals("Groceries", saved.getName());
        assertEquals(new BigDecimal("500.00"), saved.getAmount());
    }

    @Test
    void shouldGetAllTransactionsForUser() {
        createTestTransaction("Salary", "50000", "INCOME", testUser);
        createTestTransaction("Groceries", "500", "EXPENSE", testUser);
        createTestTransaction("Other Salary", "30000", "INCOME", otherUser);

        List<Transaction> transactions = transactionService.getAll(testUser);

        assertEquals(2, transactions.size());
    }

    @Test
    void shouldNotSeeOtherUsersTransactions() {
        createTestTransaction("My Expense", "100", "EXPENSE", testUser);
        createTestTransaction("Their Expense", "200", "EXPENSE", otherUser);

        List<Transaction> mine = transactionService.getAll(testUser);
        List<Transaction> theirs = transactionService.getAll(otherUser);

        assertEquals(1, mine.size());
        assertEquals("My Expense", mine.get(0).getName());
        assertEquals(1, theirs.size());
        assertEquals("Their Expense", theirs.get(0).getName());
    }

    @Test
    void shouldGetTransactionsByType() {
        createTestTransaction("Salary", "50000", "INCOME", testUser);
        createTestTransaction("Groceries", "500", "EXPENSE", testUser);
        createTestTransaction("Freelance", "10000", "INCOME", testUser);

        List<Transaction> income = transactionService.getByType(testUser, "INCOME");
        List<Transaction> expenses = transactionService.getByType(testUser, "EXPENSE");

        assertEquals(2, income.size());
        assertEquals(1, expenses.size());
    }

    @Test
    void shouldGetTransactionById() {
        Transaction created = createTestTransaction(
                "Groceries", "500", "EXPENSE", testUser);

        Transaction found = transactionService.getById(created.getId(), testUser);

        assertEquals("Groceries", found.getName());
    }

    @Test
    void shouldNotGetOtherUsersTransactionById() {
        Transaction otherTransaction = createTestTransaction(
                "Their Expense", "200", "EXPENSE", otherUser);

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.getById(otherTransaction.getId(), testUser);
        });
    }

    @Test
    void shouldUpdateTransaction() {
        Transaction created = createTestTransaction(
                "Groceries", "500", "EXPENSE", testUser);

        Transaction updated = new Transaction(
                "Updated Groceries", new BigDecimal("750.00"),
                LocalDate.now(), "Food", "EXPENSE", "Updated"
        );

        Transaction result = transactionService.update(
                created.getId(), updated, testUser);

        assertEquals("Updated Groceries", result.getName());
        assertEquals(new BigDecimal("750.00"), result.getAmount());
    }

    @Test
    void shouldNotUpdateOtherUsersTransaction() {
        Transaction otherTransaction = createTestTransaction(
                "Their Expense", "200", "EXPENSE", otherUser);

        Transaction updated = new Transaction(
                "Hacked", new BigDecimal("999"), LocalDate.now(),
                "Food", "EXPENSE", null
        );

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.update(otherTransaction.getId(), updated, testUser);
        });
    }

    @Test
    void shouldDeleteTransaction() {
        Transaction created = createTestTransaction(
                "Groceries", "500", "EXPENSE", testUser);

        transactionService.delete(created.getId(), testUser);

        List<Transaction> remaining = transactionService.getAll(testUser);
        assertEquals(0, remaining.size());
    }

    @Test
    void shouldNotDeleteOtherUsersTransaction() {
        Transaction otherTransaction = createTestTransaction(
                "Their Expense", "200", "EXPENSE", otherUser);

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.delete(otherTransaction.getId(), testUser);
        });
    }

    @Test
    void shouldCalculateTotalIncome() {
        createTestTransaction("Salary", "50000", "INCOME", testUser);
        createTestTransaction("Freelance", "10000", "INCOME", testUser);
        createTestTransaction("Groceries", "500", "EXPENSE", testUser);

        BigDecimal totalIncome = transactionService.getTotalIncome(testUser);

        assertEquals(new BigDecimal("60000.00"), totalIncome);
    }

    @Test
    void shouldCalculateTotalExpense() {
        createTestTransaction("Salary", "50000", "INCOME", testUser);
        createTestTransaction("Groceries", "500", "EXPENSE", testUser);
        createTestTransaction("Bills", "2000", "EXPENSE", testUser);

        BigDecimal totalExpense = transactionService.getTotalExpense(testUser);

        assertEquals(new BigDecimal("2500.00"), totalExpense);
    }

    @Test
    void shouldCalculateBalance() {
        createTestTransaction("Salary", "50000", "INCOME", testUser);
        createTestTransaction("Groceries", "500", "EXPENSE", testUser);

        BigDecimal balance = transactionService.getBalance(testUser);

        assertEquals(new BigDecimal("49500.00"), balance);
    }

    @Test
    void shouldReturnZeroForNoTransactions() {
        BigDecimal income = transactionService.getTotalIncome(testUser);
        BigDecimal expense = transactionService.getTotalExpense(testUser);
        BigDecimal balance = transactionService.getBalance(testUser);

        assertEquals(BigDecimal.ZERO, income);
        assertEquals(BigDecimal.ZERO, expense);
        assertEquals(BigDecimal.ZERO, balance);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTransaction() {
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.delete(99999L, testUser);
        });
    }

    // Helper method
    private Transaction createTestTransaction(String name, String amount,
                                              String type, User user) {
        Transaction t = new Transaction(
                name, new BigDecimal(amount),
                LocalDate.now(), "Food", type, null
        );
        return transactionService.create(t, user);
    }
}