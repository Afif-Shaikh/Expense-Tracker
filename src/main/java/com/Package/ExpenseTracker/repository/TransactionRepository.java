package com.Package.ExpenseTracker.repository;

import com.Package.ExpenseTracker.model.Transaction;
import com.Package.ExpenseTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);

    List<Transaction> findByUserAndTypeIgnoreCaseOrderByDateDesc(User user, String type);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);
}