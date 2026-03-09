package com.Package.ExpenseTracker.repository;

import com.Package.ExpenseTracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTypeIgnoreCaseOrderByDateDesc(String type);

    List<Transaction> findAllByOrderByDateDesc();

    List<Transaction> findByCategoryIgnoreCaseOrderByDateDesc(String category);

    List<Transaction> findByTypeAndCategoryIgnoreCaseOrderByDateDesc(
            String type, String category
    );
}