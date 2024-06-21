package com.budgetbuddy.util;

import com.budgetbuddy.exceptions.UnableToReadException;
import com.budgetbuddy.controller.UserController;
import com.budgetbuddy.models.Category;
import com.budgetbuddy.models.CategoryType;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public final class DatabaseConfiguration {
    private static final String URL;
    private static final String DATABASE_NAME;
    private static final String DATABASE_USER;
    private static final String DATABASE_PASSWORD;
    private static final String INITIAL_URL = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=UTF-8";

    private DatabaseConfiguration(){

    }

    static {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfiguration.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            properties.load(input);
            DATABASE_NAME = properties.getProperty("db.name");
            DATABASE_USER= properties.getProperty("db.user");
            DATABASE_PASSWORD = properties.getProperty("db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");

            URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?useUnicode=true&characterEncoding=UTF-8";

        } catch (IOException | ClassNotFoundException ex) {
            throw new UnableToReadException(ex.getMessage());
        }
    }

    public static void createDatabase() throws SQLException {
        try(Statement statement = getInitialConnection().createStatement()){
            statement.executeUpdate(String.format(Queries.getCreateDatabaseQuery(), DATABASE_NAME));
        }
    }

    public static void createTables() throws SQLException {
        String[] table_names = new String[]{"users", "referral_accounts", "budget", "categories", "expenses", "income", "transactions", "expense_groups", "group_members"};
        PreparedStatement statement;

        try (Connection connection = getConnection()) {
            for (String table_name : table_names) {
                switch (table_name) {
                    case "users":
                        statement = connection.prepareStatement(Queries.getCreateUserTable());
                        break;
                    case "referral_accounts":
                        statement = connection.prepareStatement(Queries.getCreateReferralTable());
                        break;
                    case "budget":
                        statement = connection.prepareStatement(Queries.getCreateBudgetTable());
                        break;
                    case "categories":
                        statement = connection.prepareStatement(Queries.getCreateCategoriesTable());
                        break;
                    case "expenses":
                        statement = connection.prepareStatement(Queries.getCreateExpenseTable());
                        break;
                    case "income":
                        statement = connection.prepareStatement(Queries.getCreateIncomeTable());
                        break;
                    case "transactions":
                        statement = connection.prepareStatement(Queries.getCreateTransactionTable());
                        break;
                    case "expense_groups":
                        statement = connection.prepareStatement(Queries.getCreateGroupTable());
                        break;
                    case "group_members" :
                        statement = connection.prepareStatement(Queries.getCreateGroupmembersTable());
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected table name: " + table_name);
                }
                statement.executeUpdate();

            }
        }
    }

    public static Connection getConnection() throws SQLException {
       return DriverManager.getConnection(URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    public static Connection getInitialConnection() throws SQLException{
        return DriverManager.getConnection(INITIAL_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    public static void insertCategory() throws SQLException {
        Map<String, List<String>> categoryList = new HashMap<>();
        List<String> expenseCategories = new ArrayList<>();
        List<String> incomeCategories = new ArrayList<>();

        // Adding expense categories
        expenseCategories.add("Food & Drinks");
        expenseCategories.add("Fuel");
        expenseCategories.add("EMI");
        expenseCategories.add("Investment");
        expenseCategories.add("Entertainment");
        expenseCategories.add("Groceries");
        expenseCategories.add("Bills");
        expenseCategories.add("Health");
        expenseCategories.add("Shopping");
        expenseCategories.add("Travel");
        expenseCategories.add("Other Expenses");

        categoryList.put("Expense", expenseCategories);

        // Adding income categories
        incomeCategories.add("Credit");

        categoryList.put("Income", incomeCategories);

        // Check if categories already exist
        String checkQuery = "SELECT COUNT(*) FROM categories";
        try (Connection connection = DatabaseConfiguration.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            ResultSet resultSet = checkStatement.executeQuery();

            resultSet.next();
            int count = resultSet.getInt(1);

            List<String> categories;
            CategoryType categoryType;
            // If there are no categories, insert them
            if (count == 0) {
                for (Map.Entry<String, List<String>> entry : categoryList.entrySet()) {
                    categories = entry.getValue();

                    if (entry.getKey().equalsIgnoreCase(CategoryType.Expense.toString())) {
                        categoryType = CategoryType.Expense;
                    } else {
                        categoryType = CategoryType.Income;
                    }

                    try (PreparedStatement preparedStatement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getAddCategories())) {
                        for (String category : categories) {
                            preparedStatement.setString(1, category);
                            preparedStatement.setString(2, categoryType.toString());
                            preparedStatement.executeUpdate();
                        }
                    }
                }
            }
        }
    }
}

