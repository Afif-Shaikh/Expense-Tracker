package com.Package.ExpenseTracker.repository;

import com.Package.ExpenseTracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // Fetch all income sorted by date (latest first)
    @Query("SELECT i FROM Income i ORDER BY i.date DESC")
    List<Income> findAllIncomeSorted();
}
