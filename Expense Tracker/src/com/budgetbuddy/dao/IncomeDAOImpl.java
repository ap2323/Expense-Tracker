package com.budgetbuddy.dao;
import com.budgetbuddy.models.Category;
import com.budgetbuddy.models.Income;
import com.budgetbuddy.service.CategoryService;

import java.sql.*;
import java.sql.Date;
import java.time.YearMonth;
import java.util.*;

import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.util.Queries;

public class IncomeDAOImpl implements IIncomeDAO{
    private final CategoryService categoryService = new CategoryService();

    private static IncomeDAOImpl iIncomeDAOImpl = null;

    private IncomeDAOImpl(){

    }

    public static IIncomeDAO getInstance(){
        if(iIncomeDAOImpl == null){
            iIncomeDAOImpl = new IncomeDAOImpl();
        }

        return iIncomeDAOImpl;
    }
    public boolean add(Income income) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getConnection()) {
            PreparedStatement incomeStatement = connection.prepareStatement(Queries.getAddIncomeQuery());
            incomeStatement.setInt(1, income.getUserId());
            incomeStatement.setInt(2, income.getCategory().getCategoryId());
            incomeStatement.setFloat(3, income.getAmount());
            incomeStatement.setString(4, income.getDescription());
            incomeStatement.setDate(5, income.getDate());
            incomeStatement.setTime(6, income.getTime());

            return incomeStatement.executeUpdate() > 0;
        }
    }

    public float get(int userId, YearMonth yearMonth) throws SQLException{
        float totalIncome = 0;

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getTotalIncomeQuery())) {
            stmt.setInt(1, userId);
            stmt.setInt(2, yearMonth.getYear());
            stmt.setInt(3, yearMonth.getMonthValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalIncome = rs.getFloat("total");
            }
        }
        return totalIncome;
    }

    private Income mapIncome(ResultSet rs) throws SQLException {
        Category category = categoryService.getCategory(rs.getInt("category_id"));
        Income income = new Income(rs.getInt("income_id"),
                category,
                rs.getInt("user_id"),
                rs.getFloat("amount"));
        income.setDate(rs.getDate("date"));
        income.setDescription(rs.getString("description"));
        income.setTime(rs.getTime("time"));
        return income;
    }

    public void remove(int incomeId) throws SQLException{

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getRemoveIncomeQuery())) {

            stmt.setInt(1, incomeId);
            stmt.executeUpdate();

        }
    }

    public List<Income> getByMonth(int userId, YearMonth yearMonth) throws SQLException{
        List<Income> incomesByMonth = new ArrayList<>();

        try(Connection connection = DatabaseConfiguration.getConnection();
        PreparedStatement statement = connection.prepareStatement(Queries.getIncomesForMonthQuery())) {

            statement.setInt(1, userId);
            statement.setInt(2, yearMonth.getMonthValue());
            statement.setInt(3, yearMonth.getYear());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                incomesByMonth.add(mapIncome(resultSet));
            }
        }

        return incomesByMonth;
    }

    public void update(int id, Income newIncome) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(Queries.getUpdateIncomeQuery());
            statement.setInt(1, newIncome.getCategory().getCategoryId());
            statement.setFloat(2, newIncome.getAmount());
            statement.setString(3, newIncome.getDescription());
            statement.setDate(4,newIncome.getDate());
            statement.setTime(5, newIncome.getTime());
            statement.setInt(6, id);
            statement.executeUpdate();
        }
    }

    public int getId(int userId, int categoryId, float amount, String description, Date date, Time time) throws SQLException{

        int incomeId = 0;
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getIncomeIdQuery())){

            statement.setInt(1,userId);
            statement.setInt(2,categoryId);
            statement.setFloat(3, amount);
            statement.setString(4, description);
            statement.setString(5, date.toString());
            statement.setTime(6, time);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                incomeId = resultSet.getInt("income_id");
            }

        }
        return incomeId;
    }

    public void delete(int userId) throws SQLException {

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getDeleteIncomeQuery())) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}

