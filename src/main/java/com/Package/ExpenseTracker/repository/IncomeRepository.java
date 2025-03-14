package com.Package.ExpenseTracker.repository;

import com.Package.ExpenseTracker.model.Income;
import com.Package.ExpenseTracker.model.User; // Import User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
}
