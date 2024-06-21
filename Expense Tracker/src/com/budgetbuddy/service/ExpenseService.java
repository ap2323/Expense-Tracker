package com.budgetbuddy.service;

import com.budgetbuddy.dao.ExpenseDAOImpl;
import com.budgetbuddy.dao.IExpenseDAO;
import com.budgetbuddy.models.Expense;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.YearMonth;
import java.util.List;

public class ExpenseService {
    private final IExpenseDAO expenseDAO;

    public ExpenseService() {
        this.expenseDAO = ExpenseDAOImpl.getInstance();
    }

    public boolean addExpense(Expense expense) throws SQLException {
        return expenseDAO.add(expense);
    }

    public float getTotalExpenses(int userId, YearMonth yearMonth) throws SQLException{
        return expenseDAO.get(userId, yearMonth);
    }

    public void removeExpense(int expenseId) throws SQLException {
        expenseDAO.remove(expenseId);
    }

    public List<Expense> getExpensesByMonth(int userId, YearMonth yearMonth) throws SQLException{
        return expenseDAO.getByMonth(userId, yearMonth);
    }

    public void updateExpense(int id, Expense newExpense) throws SQLException {
        expenseDAO.update(id, newExpense);
    }

    public int getExpenseId(int userId, int categoryId, float amount, String description, Date expense_date, Time time) throws SQLException{
        return expenseDAO.getId(userId, categoryId, amount, description, expense_date, time);
    }

    public void delete(int userId) throws SQLException{
        expenseDAO.delete(userId);
    }

    public List<Expense> getGroupExpenses(int groupId) throws SQLException{
        return expenseDAO.getGroupExpenses(groupId);
    }
}

