package com.budgetbuddy.dao;

import com.budgetbuddy.models.*;
import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.util.Queries;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements ITransactionDAO{
    private static TransactionDAOImpl transactionDAOImpl = null;

    private TransactionDAOImpl(){

    }
    public static ITransactionDAO getInstance() {
        if(transactionDAOImpl ==  null){
            transactionDAOImpl = new TransactionDAOImpl();
        }

        return transactionDAOImpl;
    }

    public boolean addTransaction(Object object) throws SQLException{
        Expense expense;
        Income income;
        if(object instanceof Expense){
            expense = (Expense) object;
            try (Connection connection = DatabaseConfiguration.getConnection()) {

                PreparedStatement statement = connection.prepareStatement(Queries.getAddTransactionQuery());
                statement.setInt(1, expense.getUserId());
                statement.setInt(2, expense.getCategory().getCategoryId());
                statement.setFloat(3, expense.getAmount());
                statement.setString(4, expense.getDescription());
                statement.setDate(5, expense.getDate());
                statement.setTime(6, expense.getTime());


                return statement.executeUpdate() > 0;
            }
        } else if (object instanceof Income) {
            income = (Income) object;
            try (Connection connection = DatabaseConfiguration.getConnection()) {

                PreparedStatement statement = connection.prepareStatement(Queries.getAddTransactionQuery());
                statement.setInt(1, income.getUserId());
                statement.setInt(2, income.getCategory().getCategoryId());
                statement.setFloat(3, income.getAmount());
                statement.setString(4, income.getDescription());
                statement.setDate(5, income.getDate());
                statement.setTime(6, income.getTime());

                return statement.executeUpdate() > 0;
            }
        }
        return false;
    }

    public boolean updateTransaction(Transaction newTransaction, Transaction oldTransaction) throws SQLException {


        try (Connection connection = DatabaseConfiguration.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(Queries.getUpdateTransactionQuery());
            statement.setInt(1, newTransaction.getCategory().getCategoryId());
            statement.setFloat(2, newTransaction.getAmount());
            statement.setString(3, newTransaction.getDescription());
            statement.setDate(4, newTransaction.getDate());
            statement.setTime(5, newTransaction.getTime());

            statement.setInt(6, oldTransaction.getUserId());
            statement.setInt(7, oldTransaction.getCategory().getCategoryId());
            statement.setFloat(8, oldTransaction.getAmount());
            statement.setString(9, oldTransaction.getDescription());
            statement.setString(10, String.valueOf(oldTransaction.getDate()));
            statement.setTime(11, oldTransaction.getTime());

            return statement.executeUpdate() > 0;
        }
    }
    public List<Transaction> getRecentTransactions(int userId, YearMonth yearMonth ,int limit) throws SQLException{
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getRecentTransactionsQuery())) {
            stmt.setInt(1, userId);
            stmt.setInt(2, yearMonth.getYear());
            stmt.setInt(3, yearMonth.getMonthValue());
            stmt.setInt(4, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = mapTransaction(rs);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Category category = new Category( rs.getInt("category_id"), rs.getString("category_name"), CategoryType.valueOf(rs.getString("category_type")));
        Transaction transaction = new Transaction(rs.getInt("transaction_id"),
                                                rs.getInt("user_id"),
                                                category,
                                                rs.getFloat("amount")
                                                );
        transaction.setDescription(rs.getString("description"));
        transaction.setDate(rs.getDate("transaction_date"));
        transaction.setTime(rs.getTime("time"));
        return transaction;
    }

    public void removeTransaction(int transactionId) throws SQLException{

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getRemoveTransactionQuery())) {

            stmt.setInt(1, transactionId);

            stmt.executeUpdate();

        }
    }

    public List<Transaction> getTransactions(int userId, YearMonth yearMonth) throws SQLException{
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement stmt = DatabaseConfiguration.getConnection().prepareStatement(Queries.getAllTransactionsQuery())) {
            stmt.setInt(1, userId);
            stmt.setInt(2, yearMonth.getMonthValue());
            stmt.setInt(3, yearMonth.getYear());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }
        }
        return transactions;
    }

    public void delete(int userId) throws SQLException{
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getDeleteTransactionQuery())) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}

