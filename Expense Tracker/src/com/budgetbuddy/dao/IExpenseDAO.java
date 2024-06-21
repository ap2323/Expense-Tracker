package com.budgetbuddy.dao;

import com.budgetbuddy.models.Expense;
import com.budgetbuddy.models.Income;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

public interface IExpenseDAO extends AbstractDAO<Expense>{

    List<Expense> getGroupExpenses(int groupId) throws SQLException;
}
