package com.budgetbuddy.util;

public final class Queries {
    // Database Queries
    private static final String CREATE_DATABASE_QUERY = "CREATE DATABASE IF NOT EXISTS %s";
    // Table Queries
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users ( " +
                                            "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                            "username VARCHAR(50) NOT NULL, " +
                                            "email VARCHAR(100) NOT NULL UNIQUE, " +
                                            "password VARCHAR(100) NOT NULL, " +
                                            "status VARCHAR(10) NOT NULL, " +
                                            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                            "updated_at DATETIME DEFAULT NULL)";

    private static final String CREATE_REFERRAL_TABLE = "CREATE TABLE IF NOT EXISTS referral_accounts ( " +
                                            "user_id INT NOT NULL, " +
                                            "referral_id INT NOT NULL, " +
                                            "FOREIGN KEY (user_id) REFERENCES users(user_id) )";

    private static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS categories ( " +
                                                "category_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                "category_name VARCHAR(50) NOT NULL, " +
                                                "category_type ENUM('Expense','Income') NOT NULL )";

    private static final String CREATE_BUDGET_TABLE = "CREATE TABLE IF NOT EXISTS budgets ( " +
                                                "budget_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                "user_id INT NOT NULL, " +
                                                "amount DECIMAL(10, 2) NOT NULL, " +
                                                "date DATE, " +
                                                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                                                "UNIQUE (user_id) )";

    private static final String CREATE_EXPENSE_TABLE = "CREATE TABLE IF NOT EXISTS expenses ( " +
                                                "expense_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                "user_id INT NOT NULL, " +
                                                "category_id INT NOT NULL, " +
                                                "amount DECIMAL(10, 2) NOT NULL, " +
                                                "description TEXT, " +
                                                "expense_date DATE NOT NULL, " +
                                                "time TIME NOT NULL, " +
                                                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                                                "FOREIGN KEY (category_id) REFERENCES categories(category_id) )";

    private static final String CREATE_INCOME_TABLE = "CREATE TABLE IF NOT EXISTS income ( " +
                                                "income_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                "user_id INT NOT NULL, " +
                                                "category_id INT NOT NULL, " +
                                                "amount DECIMAL(10, 2) NOT NULL, " +
                                                "description TEXT, " +
                                                "date DATE, " +
                                                "time TIME NOT NULL, " +
                                                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                                                "FOREIGN KEY (category_id) REFERENCES categories(category_id) )";

    private static final String CREATE_TRANSACTION_TABLE  = "CREATE TABLE IF NOT EXISTS transactions ( " +
                                                "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                "user_id INT NOT NULL, " +
                                                "category_id INT, " +
                                                "amount DECIMAL(10, 2) NOT NULL, " +
                                                "description TEXT, " +
                                                "transaction_date DATE, " +
                                                "time TIME NOT NULL, " +
                                                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                                                "FOREIGN KEY (category_id) REFERENCES categories(category_id) )";

    private static final String CREATE_GROUP_TABLE= "CREATE TABLE IF NOT EXISTS expense_groups ( " +
                                    "group_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "group_name VARCHAR(50) NOT NULL )";


    private static final String CREATE_GROUPMEMBERS_TABLE ="CREATE TABLE IF NOT EXISTS group_members ( "+
                                            "group_id INT NOT NULL, " +
                                            "user_id INT NOT NULL, " +
                                            "expense_id INT NOT NULL, " +
                                            "FOREIGN KEY (group_id) REFERENCES expense_groups(group_id), " +
                                            "FOREIGN KEY (user_id) REFERENCES users(user_id) )";


    // Admin Queries
    private static final String GET_TOTAL_USERS = "SELECT username, created_at FROM users ORDER BY created_at DESC";
    private static final String GET_REMOVED_USERS = "SELECT username, created_at FROM users WHERE status = 'removed' ORDER BY created_at DESC";
    private static final String GET_ACTIVE_USERS = "SELECT username, created_at FROM users WHERE status = 'active' ORDER BY created_at DESC";

    // User Queries
    private static final String LOGIN_QUERY = "SELECT * FROM users WHERE email = ? AND password = ?";
    private static final String REGISTER_QUERY = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";
    private static final String IS_ALREADY_REGISTER_QUERY = "SELECT user_id FROM users WHERE email = ?";
    private static final String IS_VALID_USERNAME_QUERY = "SELECT COUNT(username) FROM users WHERE username = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET username = ?, email = ?, password = ?, updated_at = ? WHERE user_id = ?";
    private static final String UPDATE_STATUS_QUERY = "UPDATE users SET status = ? WHERE user_id = ?";
    private static final String GET_USER_ID_QUERY = "SELECT user_id FROM users WHERE email = ?";
    private static final String REGISTER_REFERRAL_QUERY = "INSERT INTO referral_accounts VALUES(?, ?)";
    private static final String GET_REFERRAL_USERS_QUERY = "SELECT u.user_id, u.username, u.email, u.password, u.status, u.created_at, u.updated_at FROM users u " +
            "LEFT JOIN referral_accounts r ON r.referral_id = u.user_id WHERE r.user_id = ?";

    private static final String IS_ALREADY_REFERRED_QUERY = "SELECT COUNT(*) from referral_accounts WHERE user_id = ? AND referral_id = ?";

    // Budget Queries
    private static final String SET_BUDGET_QUERY = "INSERT INTO budgets (user_id, amount, date) VALUES (?, ?, NOW())";
    private static final String UPDATE_BUDGET_QUERY = "UPDATE budgets SET amount = ?, date = NOW() WHERE user_id = ?";
    private static final String GET_USER_BUDGET_QUERY = "SELECT amount FROM budgets WHERE user_id = ?";
    private static final String IS_ALREADY_EXISTS_QUERY = "SELECT user_id FROM budgets WHERE user_id = ?";
    private static final String DELETE_BUDGET_QUERY = "DELETE FROM budgets WHERE user_id = ?";

    // Category Queries
    private static final String ADD_CATEGORIES = "INSERT INTO categories(category_name, category_type) VALUES(?, ?)";
    private static final String GET_ALL_CATEGORIES_QUERY = "SELECT * FROM categories";
    private static final String GET_CATEGORY_QUERY = "SELECT * FROM categories WHERE category_id=?";

    // Expense Queries
    private static final String ADD_EXPENSE_QUERY = "INSERT INTO expenses (user_id, category_id, amount, description, expense_date, time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_TOTAL_EXPENSE_QUERY = "SELECT SUM(amount) AS total FROM expenses WHERE user_id = ? AND YEAR(expense_date) = ? AND MONTH(expense_date) = ?";
    private static final String REMOVE_EXPENSE_QUERY = "DELETE FROM expenses WHERE expense_id = ?";
    private static final String DELETE_EXPENSES_QUERY = "DELETE FROM expenses WHERE user_id = ?";
    private static final String GET_EXPENSES_FOR_MONTH_QUERY = "SELECT * FROM expenses WHERE user_id = ? AND MONTH(expense_date) = ? AND YEAR(expense_date) = ? ORDER BY time DESC";
    private static final String UPDATE_EXPENSE_QUERY = "UPDATE expenses SET category_id=?, amount=?, description=?, expense_date=?, time = ? WHERE expense_id = ?";
    private static final String GET_EXPENSE_ID_QUERY = "SELECT expense_id FROM expenses WHERE user_id = ? AND category_id = ? AND amount= ? AND description=? AND expense_date = ? AND time = ?";

    // Income Queries
    private static final String ADD_INCOME_QUERY = "INSERT INTO income (user_id, category_id ,amount, description, date, time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_TOTAL_INCOME_QUERY = "SELECT SUM(amount) AS total FROM income WHERE user_id = ? AND YEAR(date) = ? AND MONTH(date) = ?";
    private static final String REMOVE_INCOME_QUERY = "DELETE FROM income WHERE income_id = ?";
    private static final String DELETE_INCOME_QUERY = "DELETE FROM income WHERE user_id = ?";
    private static final String GET_INCOMES_FOR_MONTH_QUERY = "SELECT * FROM income WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ? ORDER BY time DESC";
    private static final String UPDATE_INCOME_QUERY = "UPDATE income SET category_id=?, amount=?, description=?, date=?, time = ?WHERE income_id=?";
    private static final String GET_INCOME_ID_QUERY = "SELECT income_id FROM income WHERE user_id = ? AND category_id = ? AND amount= ? AND description=? AND date = ? AND time = ?";

    // Transaction Queries
    private static final String ADD_TRANSACTION_QUERY = "INSERT INTO transactions(user_id, category_id, amount, description, transaction_date, time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_TRANSACTION_QUERY = "UPDATE transactions SET category_id = ?, amount = ?, description=?, transaction_date = ?, time = ? WHERE user_id = ? AND category_id =? AND amount=? AND description=? AND transaction_date=? AND time = ?";

    private static final String GET_RECENT_TRANSACTIONS_QUERY = "SELECT t.transaction_id, t.user_id, t.amount, t.description, t.transaction_date, t.time, c.category_id, c.category_name, c.category_type FROM transactions t " +
                                                                " LEFT JOIN categories c ON t.category_id = c.category_id WHERE t.user_id = ? AND YEAR(t.transaction_date) = ? AND MONTH(t.transaction_date) = ? ORDER BY time DESC LIMIT ?";

    private static final String REMOVE_TRANSACTION_QUERY = "DELETE FROM transactions WHERE transaction_id = ?";
    private static final String DELETE_TRANSACTION_QUERY = "DELETE FROM transactions WHERE user_id = ?";

    private static final String GET_ALL_TRANSACTIONS_QUERY = "SELECT t.transaction_id, t.user_id, t.amount, t.description, t.transaction_date, t.time, " +
                                                                "c.category_id, c.category_name, c.category_type " +
                                                                "FROM transactions t LEFT JOIN categories c ON t.category_id = c.category_id " +
                                                                "WHERE t.user_id = ? AND MONTH(t.transaction_date) = ? AND YEAR(t.transaction_date) = ? ORDER BY t.time DESC";



    //Access database query
    public static String getCreateDatabaseQuery() {
        return CREATE_DATABASE_QUERY;
    }

    // Access Table Queries
    public static String getCreateUserTable() {
        return CREATE_USER_TABLE;
    }

    public static String getCreateReferralTable() {
        return CREATE_REFERRAL_TABLE;
    }

    public static String getCreateCategoriesTable() {
        return CREATE_CATEGORIES_TABLE;
    }

    public static String getCreateBudgetTable() {
        return CREATE_BUDGET_TABLE;
    }

    public static String getCreateIncomeTable() {
        return CREATE_INCOME_TABLE;
    }

    public static String getCreateExpenseTable() {
        return CREATE_EXPENSE_TABLE;
    }

    public static String getCreateTransactionTable() {
        return CREATE_TRANSACTION_TABLE;
    }

    public static String getCreateGroupTable() {
        return CREATE_GROUP_TABLE;
    }

    public static String getCreateGroupmembersTable() {
        return CREATE_GROUPMEMBERS_TABLE;
    }

    public static String getTotalUsers() {
        return GET_TOTAL_USERS;
    }

    public static String getRemovedUsers() {
        return GET_REMOVED_USERS;
    }

    public static String getActiveUsers() {
        return GET_ACTIVE_USERS;
    }

    // Access User Queries
    public static String getLogin_Query(){
        return LOGIN_QUERY;
    }

    public static String getRegisterQuery(){
        return REGISTER_QUERY;
    }

    public static String getIsAlreadyRegisterQuery(){
        return IS_ALREADY_REGISTER_QUERY;
    }

    public static String getIsValidUsernameQuery() {
        return IS_VALID_USERNAME_QUERY;
    }

    public static String getUpdateQuery() {
        return UPDATE_QUERY;
    }
    public static String getUpdateStatusQuery() {
        return UPDATE_STATUS_QUERY;
    }

    public static String getReferralUsersQuery() {
        return GET_REFERRAL_USERS_QUERY;
    }

    public static String getUserIdQuery() {
        return GET_USER_ID_QUERY;
    }

    public static String getIsAlreadyReferredQuery() {
        return IS_ALREADY_REFERRED_QUERY;
    }

    public static String getRegisterReferralQuery() {
        return REGISTER_REFERRAL_QUERY;
    }


    // Access Budget Queries
    public static String getSetBudgetQuery(){
        return SET_BUDGET_QUERY;
    }

    public static String getUpdateBudgetQuery(){
        return UPDATE_BUDGET_QUERY;
    }

    public static String getUserBudgetQuery(){
        return  GET_USER_BUDGET_QUERY;
    }

    public static String getIsAlreadyExistsQuery(){
        return IS_ALREADY_EXISTS_QUERY;
    }

    public static String getDeleteBudgetQuery() {
        return DELETE_BUDGET_QUERY;
    }

    // Access Category Queries
    public static String getAddCategories() {
        return ADD_CATEGORIES;
    }

    public static String getAllCategoriesQuery(){
        return GET_ALL_CATEGORIES_QUERY;
    }

    public static String getCategoryQuery(){
        return GET_CATEGORY_QUERY;
    }

    // Access Expense Queries
    public static String getAddExpenseQuery() {
        return ADD_EXPENSE_QUERY;
    }

    public static String getTotalExpenseQuery() {
        return GET_TOTAL_EXPENSE_QUERY;
    }

    public static String getExpensesForMonthQuery() {
        return GET_EXPENSES_FOR_MONTH_QUERY;
    }

    public static String getUpdateExpenseQuery() {
        return UPDATE_EXPENSE_QUERY;
    }

    public static String getRemoveExpenseQuery() {
        return REMOVE_EXPENSE_QUERY;
    }

    public static String getDeleteExpensesQuery() {
        return DELETE_EXPENSES_QUERY;
    }

    public static String getExpenseIdQuery() {
        return GET_EXPENSE_ID_QUERY;
    }

    // Access Income Queries
    public static String getAddIncomeQuery() {
        return ADD_INCOME_QUERY;
    }

    public static String getTotalIncomeQuery() {
        return GET_TOTAL_INCOME_QUERY;
    }

    public static String getIncomesForMonthQuery() {
        return GET_INCOMES_FOR_MONTH_QUERY;
    }

    public static String getUpdateIncomeQuery() {
        return UPDATE_INCOME_QUERY;
    }

    public static String getRemoveIncomeQuery() {
        return REMOVE_INCOME_QUERY;
    }

    public static String getDeleteIncomeQuery() {
        return DELETE_INCOME_QUERY;
    }

    public static String getIncomeIdQuery() {
        return GET_INCOME_ID_QUERY;
    }

    //Access Transaction Queries
    public static String getAddTransactionQuery() {
        return ADD_TRANSACTION_QUERY;
    }

    public static String getUpdateTransactionQuery() {
        return UPDATE_TRANSACTION_QUERY;
    }

    public static String getRemoveTransactionQuery() {
        return REMOVE_TRANSACTION_QUERY;
    }

    public static String getDeleteTransactionQuery() {
        return DELETE_TRANSACTION_QUERY;
    }

    public static String getRecentTransactionsQuery() {
        return GET_RECENT_TRANSACTIONS_QUERY;
    }

    public static String getAllTransactionsQuery() {
        return GET_ALL_TRANSACTIONS_QUERY;
    }
}
