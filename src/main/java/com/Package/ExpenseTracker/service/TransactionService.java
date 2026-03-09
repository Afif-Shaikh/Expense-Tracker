package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAllByOrderByDateDesc();
    }

    public List<Transaction> getByType(String type) {
        return transactionRepository.findByTypeIgnoreCaseOrderByDateDesc(type);
    }

    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    public BigDecimal getTotalIncome() {
        return transactionRepository
                .findByTypeIgnoreCaseOrderByDateDesc("INCOME")
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense() {
        return transactionRepository
                .findByTypeIgnoreCaseOrderByDateDesc("EXPENSE")
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }
}