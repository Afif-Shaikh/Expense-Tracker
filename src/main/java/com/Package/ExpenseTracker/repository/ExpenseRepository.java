package com.Package.ExpenseTracker.repository;

import com.Package.ExpenseTracker.model.Expense;
import com.Package.ExpenseTracker.model.User; // Import User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user); 
}
