package com.budgetbuddy.dao;

import com.budgetbuddy.models.Budget;

import java.sql.SQLException;

public interface IBudgetDAO {
    boolean setBudget(Budget budget) throws SQLException;
    boolean update(Budget budget) throws SQLException;
    float getUserBudget(int userId) throws SQLException;
    boolean isAlreadyExists(int userId) throws SQLException;
    void delete(int userId) throws SQLException;
}
