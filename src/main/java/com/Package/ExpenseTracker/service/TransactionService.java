package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.exception.TransactionNotFoundException;
import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction, User user) {
        log.info("Creating transaction for user={}: name={}, type={}, amount={}",
                user.getEmail(), transaction.getName(),
                transaction.getType(), transaction.getAmount());
        transaction.setUser(user);
        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: id={}", saved.getId());
        return saved;
    }

    public List<Transaction> getAll(User user) {
        log.info("Fetching all transactions for user={}", user.getEmail());
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    public List<Transaction> getByType(User user, String type) {
        log.info("Fetching transactions by type={} for user={}", type, user.getEmail());
        return transactionRepository.findByUserAndTypeIgnoreCaseOrderByDateDesc(user, type);
    }

    public Transaction getById(Long id, User user) {
        log.info("Fetching transaction id={} for user={}", id, user.getEmail());
        return transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction update(Long id, Transaction updated, User user) {
        log.info("Updating transaction id={} for user={}", id, user.getEmail());
        Transaction existing = getById(id, user);

        existing.setName(updated.getName());
        existing.setAmount(updated.getAmount());
        existing.setDate(updated.getDate());
        existing.setCategory(updated.getCategory());
        existing.setType(updated.getType());
        existing.setComments(updated.getComments());

        Transaction saved = transactionRepository.save(existing);
        log.info("Transaction updated: id={}", saved.getId());
        return saved;
    }

    public void delete(Long id, User user) {
        log.info("Deleting transaction id={} for user={}", id, user.getEmail());
        if (!transactionRepository.existsByIdAndUser(id, user)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction deleted: id={}", id);
    }

    public BigDecimal getTotalIncome(User user) {
        return transactionRepository
                .findByUserAndTypeIgnoreCaseOrderByDateDesc(user, "INCOME")
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense(User user) {
        return transactionRepository
                .findByUserAndTypeIgnoreCaseOrderByDateDesc(user, "EXPENSE")
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalance(User user) {
        return getTotalIncome(user).subtract(getTotalExpense(user));
    }
}