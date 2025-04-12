package com.Package.ExpenseTracker.repository;

import com.Package.ExpenseTracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Fetch all expenses sorted by date (latest first)
    @Query("SELECT e FROM Expense e ORDER BY e.date DESC")
    List<Expense> findAllExpensesSorted();
    
//    List<Expense> findByUserId(Long Id); 

}
