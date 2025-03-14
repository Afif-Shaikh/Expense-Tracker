package com.Package.ExpenseTracker.service;

import com.Package.ExpenseTracker.model.Income;
import com.Package.ExpenseTracker.model.User;
import com.Package.ExpenseTracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    // 1️⃣ Add Income for a User
    public Income addIncome(Income income, User user) {
        income.setUser(user); // Associate income with the given user
        return incomeRepository.save(income);
    }

    // 2️⃣ Get All Incomes for a Specific User
    public List<Income> getUserIncomes(User user) {
        return incomeRepository.findByUser(user);
    }
    
    public List<Income> getUserIncome(User user) {
        return incomeRepository.findByUser(user);
    }


    // 3️⃣ Get a Single Income by ID (Optional)
    public Optional<Income> getIncomeById(Long id) {
        return incomeRepository.findById(id);
    }

    // 4️⃣ Delete Income by ID (Only if it belongs to the user)
    public boolean deleteIncome(Long id, User user) {
        Optional<Income> optionalIncome = incomeRepository.findById(id);

        if (optionalIncome.isPresent()) {
            Income income = optionalIncome.get();

            // Ensure the income belongs to the user before deleting
            if (income.getUser().getId().equals(user.getId())) {
                incomeRepository.deleteById(id);
                return true;
            }
        }
        return false; // Income not found OR does not belong to user
    }
}
