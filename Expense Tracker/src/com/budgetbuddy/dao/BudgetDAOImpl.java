package com.budgetbuddy.dao;

import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.models.Budget;
import com.budgetbuddy.util.Queries;

import java.sql.*;

public class BudgetDAOImpl implements IBudgetDAO{

    private static BudgetDAOImpl budgetDAOImpl = null;

    private BudgetDAOImpl(){

    }

    public static IBudgetDAO getInstance(){
        if(budgetDAOImpl == null){
            budgetDAOImpl = new BudgetDAOImpl();
        }

        return budgetDAOImpl;
    }
    public boolean setBudget(Budget budget) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(Queries.getSetBudgetQuery());
            statement.setInt(1, budget.getUserId());
            statement.setFloat(2, budget.getAmount());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(Budget budget) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(Queries.getUpdateBudgetQuery());
            statement.setFloat(1, budget.getAmount());
            statement.setInt(2, budget.getUserId());
            return statement.executeUpdate() > 0;
        }
    }

    public float getUserBudget(int userId) throws SQLException{
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getUserBudgetQuery())) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
               return rs.getFloat(1);
            }


        }
        return 0.0f;
    }

    public boolean isAlreadyExists(int userId) throws SQLException {
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getIsAlreadyExistsQuery())) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    public void delete(int userId) throws SQLException{
        if(isAlreadyExists(userId)){

            try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getDeleteBudgetQuery())){
                statement.setInt(1, userId);
                statement.executeUpdate();
            }
        }
    }
}

