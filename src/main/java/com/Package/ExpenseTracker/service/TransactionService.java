package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.exception.TransactionNotFoundException;
import com.Package.ExpenseTracker.model.Transaction;
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

    public Transaction create(Transaction transaction) {
        log.info("Creating transaction: name={}, type={}, amount={}",
                transaction.getName(), transaction.getType(), transaction.getAmount());
        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created with id={}", saved.getId());
        return saved;
    }

    public List<Transaction> getAll() {
        log.info("Fetching all transactions");
        List<Transaction> transactions = transactionRepository.findAllByOrderByDateDesc();
        log.info("Found {} transactions", transactions.size());
        return transactions;
    }

    public List<Transaction> getByType(String type) {
        log.info("Fetching transactions by type={}", type);
        List<Transaction> transactions =
                transactionRepository.findByTypeIgnoreCaseOrderByDateDesc(type);
        log.info("Found {} transactions of type {}", transactions.size(), type);
        return transactions;
    }

    public Transaction getById(Long id) {
        log.info("Fetching transaction by id={}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction update(Long id, Transaction updated) {
        log.info("Updating transaction id={}", id);
        Transaction existing = getById(id);

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

    public void delete(Long id) {
        log.info("Deleting transaction id={}", id);
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction deleted: id={}", id);
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