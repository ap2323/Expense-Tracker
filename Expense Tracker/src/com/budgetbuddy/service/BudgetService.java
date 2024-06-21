package com.budgetbuddy.service;

import com.budgetbuddy.dao.BudgetDAOImpl;
import com.budgetbuddy.dao.IBudgetDAO;
import com.budgetbuddy.models.Budget;

import java.sql.SQLException;

public class BudgetService {
    private final IBudgetDAO budgetDAO;

    public BudgetService() {
        this.budgetDAO = BudgetDAOImpl.getInstance();
    }

    public boolean setBudget(Budget budget) throws SQLException {
        if(isAlreadyExists(budget.getUserId())) {
            return updateBudget(budget);
        } else {
            return budgetDAO.setBudget(budget);
        }
    }

    private boolean isAlreadyExists(int userId) throws SQLException {
       return budgetDAO.isAlreadyExists(userId);
    }

    private boolean updateBudget(Budget budget) throws SQLException{
        return budgetDAO.update(budget);
    }

    public float getUserBudgets(int userId) throws SQLException{
        return budgetDAO.getUserBudget(userId);
    }

    public void delete(int userId) throws SQLException {
        budgetDAO.delete(userId);
    }
}

