package com.budgetbuddy.service;

import com.budgetbuddy.dao.IIncomeDAO;
import com.budgetbuddy.dao.IncomeDAOImpl;
import com.budgetbuddy.models.Income;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.YearMonth;
import java.util.List;

public class IncomeService {
    private final IIncomeDAO incomeDAO;

    public IncomeService() {
        this.incomeDAO = IncomeDAOImpl.getInstance();
    }

    public boolean addIncome(Income income) throws SQLException {
        return incomeDAO.add(income);
    }

    public float getTotalIncome(int userId, YearMonth yearMonth) throws SQLException{
        return incomeDAO.get(userId, yearMonth);
    }

    public void removeIncome(int incomeId) throws SQLException {
        incomeDAO.remove(incomeId);
    }

    public List<Income> getIncomesByMonth(int userId, YearMonth yearMonth) throws SQLException{
        return incomeDAO.getByMonth(userId, yearMonth);
    }

    public void updateIncome(int id, Income newIncome) throws SQLException {
        incomeDAO.update(id, newIncome);
    }

    public int getIncomeId(int userId, int categoryId, float amount, String description, Date transactionDate, Time time) throws SQLException{
        return incomeDAO.getId(userId, categoryId, amount, description, transactionDate, time);
    }

    public void delete(int userId) throws SQLException{
        incomeDAO.delete(userId);
    }
}

