package com.budgetbuddy.dao;

import com.budgetbuddy.models.Category;
import com.budgetbuddy.models.Expense;
import com.budgetbuddy.service.CategoryService;
import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.util.Queries;

import java.sql.*;
import java.sql.Date;
import java.time.YearMonth;
import java.util.*;

public class ExpenseDAOImpl implements IExpenseDAO {
    private final CategoryService categoryService = new CategoryService();

    private static ExpenseDAOImpl expenseDAOImpl = null;

    private ExpenseDAOImpl(){

    }

    public static IExpenseDAO getInstance(){
        if(expenseDAOImpl == null){
            expenseDAOImpl = new ExpenseDAOImpl();
        }

        return expenseDAOImpl;
    }
    public boolean add(Expense expense) throws SQLException{

        try (Connection connection = DatabaseConfiguration.getConnection()) {
            PreparedStatement expenseStatement = connection.prepareStatement(Queries.getAddExpenseQuery());
            expenseStatement.setInt(1, expense.getUserId());
            expenseStatement.setInt(2, expense.getCategory().getCategoryId());
            expenseStatement.setFloat(3, expense.getAmount());
            expenseStatement.setString(4, expense.getDescription());
            expenseStatement.setDate(5, expense.getDate());
            expenseStatement.setTime(6, expense.getTime());

            return expenseStatement.executeUpdate() > 0;
        }
    }

    public float get(int userId, YearMonth yearMonth) throws SQLException{
        float totalExpenses = 0f;
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getTotalExpenseQuery())) {
            stmt.setInt(1, userId);
            stmt.setInt(2, yearMonth.getYear());
            stmt.setInt(3, yearMonth.getMonthValue());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalExpenses = rs.getFloat("total");
            }
        }
        return totalExpenses;
    }

    public void remove(int expenseId) throws SQLException {

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getRemoveExpenseQuery())) {

            stmt.setInt(1, expenseId);

            stmt.executeUpdate();

        }
    }

    public List<Expense> getByMonth(int userId, YearMonth yearMonth) throws SQLException{
        List<Expense> expensesByMonth = new ArrayList<>();
        try(Connection connection = DatabaseConfiguration.getConnection();
            PreparedStatement statement = connection.prepareStatement(Queries.getExpensesForMonthQuery())) {

            statement.setInt(1, userId);
            statement.setInt(2, yearMonth.getMonthValue());
            statement.setInt(3, yearMonth.getYear());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                expensesByMonth.add(mapExpense(resultSet));
            }
        }

        return expensesByMonth;
    }

    private Expense mapExpense(ResultSet resultSet) throws SQLException {
        Category category = categoryService.getCategory(resultSet.getInt("category_id")); // Method to fetch category
        Expense expense = new Expense(resultSet.getInt("expense_id")
                , resultSet.getInt("user_id")
                , category,
                resultSet.getFloat("amount"));
        expense.setDescription(resultSet.getString("description"));
        expense.setExpenseDate(resultSet.getDate("expense_date"));
        expense.setTime(resultSet.getTime("time"));
        return expense;
    }

    public void update(int id, Expense newExpense) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getConnection()) {

            PreparedStatement expenseStatement = connection.prepareStatement(Queries.getUpdateExpenseQuery());
            expenseStatement.setInt(1, newExpense.getCategory().getCategoryId());
            expenseStatement.setFloat(2, newExpense.getAmount());
            expenseStatement.setString(3, newExpense.getDescription());
            expenseStatement.setDate(4,newExpense.getDate());
            expenseStatement.setTime(5, newExpense.getTime());
            expenseStatement.setInt(6, id);
            expenseStatement.executeUpdate();
        }
    }

    public int getId(int userId, int categoryId, float amount, String description, Date expense_date, Time time) throws SQLException{

        int expenseId = 0;
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getExpenseIdQuery())){

            statement.setInt(1,userId);
            statement.setInt(2,categoryId);
            statement.setFloat(3, amount);
            statement.setString(4, description);
            statement.setString(5, expense_date.toString());
            statement.setTime(6, time);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                expenseId = resultSet.getInt("expense_id");
            }

        }
        return expenseId;
    }

    public void delete(int userId) throws SQLException{

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getDeleteExpensesQuery())) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }


    public List<Expense> getGroupExpenses(int groupId) throws SQLException{
        List<Expense> expenseList = new ArrayList<>();

        String query = "SELECT e.expense_id, e.user_id, e.amount, e.description, e.expense_date, e.category_id, e.time " +
                "FROM group_members g INNER JOIN expenses e ON g.expense_id = e.expense_id WHERE g.group_id = ? ORDER BY e.time DESC";

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(query)){
            statement.setInt(1, groupId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                expenseList.add(mapExpense(resultSet));
            }
        }
        return expenseList;
    }
}
