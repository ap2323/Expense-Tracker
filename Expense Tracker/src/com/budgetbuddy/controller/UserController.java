package com.budgetbuddy.controller;

import com.budgetbuddy.exceptions.UnableToReadException;
import com.budgetbuddy.exceptions.UnableToWriteException;
import com.budgetbuddy.exceptions.UserAlreadyFoundException;
import com.budgetbuddy.exceptions.UserNotFoundException;
import com.budgetbuddy.models.*;
import com.budgetbuddy.service.*;
import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.view.MainUI;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final BudgetService budgetService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final MainUI mainUI;
    private User loggedInUser;

    private static final List<Income> incomesByMonth = new ArrayList<>();
    private static final List<Expense> expensesByMonth = new ArrayList<>();
    private static final List<Transaction> transactionsList = new ArrayList<>();
    private static final List<User> referral_users = new ArrayList<>();
    private static final String WRITE_DATA_ERROR = "Unable to save data.";
    private static final String READ_DATA_ERROR = "Unable to fetch data.";
    private static final String INVALID_MAIL_FORMAT_ERROR = "Invalid Mail Format.";
    private static final String INVALID_PASSWORD_FORMAT_ERROR = "Invalid Password Format.";

    static {
        try{
            DatabaseConfiguration.createDatabase();
            DatabaseConfiguration.createTables();
            DatabaseConfiguration.insertCategory();

        }catch (SQLException ex){
            throw new UnableToWriteException("Failed to Create Database.");
        }
    }

    public UserController() {
        userService = new UserService();
        expenseService = new ExpenseService();
        incomeService = new IncomeService();
        budgetService = new BudgetService();
        transactionService = new TransactionService();
        categoryService = new CategoryService();
        mainUI = new MainUI(this);
    }

    public void start() {
        mainUI.showLoginMenu();
    }

    public String maskEmail(String email) {
        // Split the email into username and domain parts
        int atIndex = email.indexOf('@');

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            // If the username is very short, do not mask it
            return email;
        } else {
            StringBuilder maskedUsername = new StringBuilder();
            maskedUsername.append(username.charAt(0)); // First character
            for (int i = 1; i < username.length() - 1; i++) {
                maskedUsername.append('*'); // Mask with *
            }
            maskedUsername.append(username.charAt(username.length() - 1)); // Last character
            return maskedUsername  + domain;
        }
    }

    public String maskPassword(String password) {
        // Create a char array filled with asterisks of the same length as the password
        char[] maskedPassword = new char[password.length()];
        for (int i = 0; i < password.length(); i++) {
            maskedPassword[i] = '*';
        }

        return new String(maskedPassword);
    }

    public void login(String email, String password) {
        if(!Validator.isValidMail(email)) {
            throw new IllegalArgumentException(INVALID_MAIL_FORMAT_ERROR);
        }
        if (!Validator.isValidPassword(password)){
            throw new IllegalArgumentException(INVALID_PASSWORD_FORMAT_ERROR);
        }
        User user;
        try {
           user = userService.login(email, password);
            if (user != null) {
                switch (user.getStatus()) {
                    case "sleep":
                        userService.updateStatus(user.getUserId(), "active");
                        loggedInUser = user;

                        break;
                    case "active":
                        throw new UserAlreadyFoundException("Currently in use.");
                    case "removed":
                        throw new UserNotFoundException("User not found.");
                }
            } else {
                throw new UserNotFoundException("User Not Found.");
            }
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void register(String username, String email, String password) {

        if (isValidUsername(username)) throw new UserAlreadyFoundException("Username already taken.");

        if (!isValidMail(email)) {
            throw new IllegalArgumentException(INVALID_MAIL_FORMAT_ERROR);
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD_FORMAT_ERROR);
        }
        if(isAlreadyRegistered(email)) throw new UserAlreadyFoundException("User Already Registered.");
        try {

            User user = new User(username, email, password);
            user.setStatus("sleep");
            userService.register(user);
        } catch (SQLException ex) {
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public boolean isAlreadyRegistered(String mail){
        try {
            if (userService.isAlreadyRegistered(mail)) {
                return true;
            }
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
        return false;
    }

    public boolean isValidUsername(String username) {
            try {
                return userService.isValidUsername(username);
            } catch (SQLException ex){
                throw new UnableToReadException(READ_DATA_ERROR);
            }
    }

    public boolean isValidMail(String mail){
        return Validator.isValidMail(mail);
    }

    public boolean isValidPassword(String password){
        return Validator.isValidPassword(password);
    }

    public void addTransaction(int categoryId, float amount, String description, Date date, Time time) {
        Category category = getCategoryById(categoryId);

        try {
            if (category.getCategoryType().equals(CategoryType.Expense)) {
                Expense expense = new Expense(loggedInUser.getUserId(), category, amount);
                expense.setDescription(description);
                expense.setExpenseDate(date);
                expense.setTime(getFormattedTime(time.getTime()));

                expenseService.addExpense(expense);
                transactionService.addTransaction(expense);
                mainUI.showTransactionSuccess();
            } else {
                Income income = new Income(loggedInUser.getUserId(), category, amount);
                income.setDescription(description);
                income.setDate(date);
                income.setTime(getFormattedTime(time.getTime()));

                if (incomeService.addIncome(income)) {
                    transactionService.addTransaction(income);
                    mainUI.showTransactionSuccess();
                }
            }
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }

    }

    private Time getFormattedTime(long time) {
        /*// Calculate the time in hours, minutes, and truncate seconds and milliseconds
        long hours = (time / (1000 * 60 * 60)) % 24;
        long minutes = (time / (1000 * 60)) % 60;

        // Reconstruct the time without seconds and milliseconds
        long timeWithoutSeconds = (hours * 3600 + minutes * 60) * 1000;*/

        return new Time(time / 1000 * 1000);

    }

    public Category getCategoryById(int categoryId){
        try {
            return categoryService.getCategory(categoryId);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void addExpense(Category category, float amount, String description, Time time, Date date){
        Expense expense = new Expense(loggedInUser.getUserId(), category, amount);
        expense.setDescription(description);
        expense.setExpenseDate(date);
        expense.setTime(time);

        try {
            expenseService.addExpense(expense);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void addIncome(Category category, float amount, String description, Time time, Date date) {
        Income income = new Income(loggedInUser.getUserId(), category, amount);
        income.setDescription(description);
        income.setDate(date);
        income.setTime(time);
        try {
            incomeService.addIncome(income);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void removeIncome(int incomeId) {
        try {
            incomeService.removeIncome(incomeId);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void removeExpense(int expenseId) {
        try {
            expenseService.removeExpense(expenseId);
        }catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void setBudget(float amount) {
        Budget budget = new Budget(loggedInUser.getUserId(), amount);
        try {
            budgetService.setBudget(budget);

        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }

    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public float getTotalExpenses(YearMonth yearMonth){
        try {
            return expenseService.getTotalExpenses(loggedInUser.getUserId(), yearMonth);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public List<Transaction> getRecentTransactions(int limit){
        try {
            return transactionService.getRecentTransactions(loggedInUser.getUserId(), YearMonth.now(),limit);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public float getTotalIncome(YearMonth yearMonth) {
        try {
            return incomeService.getTotalIncome(loggedInUser.getUserId(), yearMonth);
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public float getUserBudget(){
        try {
            return budgetService.getUserBudgets(loggedInUser.getUserId());
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }


    public List<Category> getCategories() {
        try {
            return categoryService.getCategories();
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void getIncomesByMonth(YearMonth yearMonth) {
        try {
            incomesByMonth.clear();
            incomesByMonth.addAll(incomeService.getIncomesByMonth(loggedInUser.getUserId(), yearMonth));
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public List<Income> getIncomesByMonth(){
        return incomesByMonth;
    }

    public void getExpensesByMonth(YearMonth yearMonth) {
        try {
            expensesByMonth.clear();
           expensesByMonth.addAll(expenseService.getExpensesByMonth(loggedInUser.getUserId(), yearMonth));
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public List<Expense> getExpensesByMonth(){
        return expensesByMonth;
    }
    public void removeTransaction(int transactionId) {
        try {
            transactionService.removeTransaction(transactionId);
        }catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void updateTransaction(Transaction newTransaction, Transaction oldTransaction) {
        try {
            transactionService.updateTransaction(newTransaction, oldTransaction);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void getTransactions(YearMonth yearMonth) {
        try {
            transactionsList.clear();
            transactionsList.addAll(transactionService.getTransactions(loggedInUser.getUserId(), yearMonth));
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public List<Transaction> getTransactionsList(){
        return transactionsList;
    }
    public int getIncomeId(int categoryId, float amount, String description, Date transactionDate, Time time) {
        try {
            return incomeService.getIncomeId(loggedInUser.getUserId(), categoryId, amount, description, transactionDate, time);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public int getExpenseId(int categoryId, float amount, String description, Date expense_date, Time time) {
        try {
            return expenseService.getExpenseId(loggedInUser.getUserId(), categoryId, amount, description, expense_date, time);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public boolean isVaildTimeFormat(String timeInput) {
        return Validator.isValidTimeFormat(timeInput);
    }

    public void update(int id, Transaction newTransaction)  {
        CategoryType type = newTransaction.getCategory().getCategoryType();
        try {
            if (type.equals(CategoryType.Expense)) {
                Expense expense = new Expense(newTransaction.getUserId(), newTransaction.getCategory(), newTransaction.getAmount());
                expense.setDescription(newTransaction.getDescription());
                expense.setTime(newTransaction.getTime());
                expense.setExpenseDate(newTransaction.getDate());

                expenseService.updateExpense(id, expense);

            } else if (type.equals(CategoryType.Income)) {
                Income income = new Income(newTransaction.getUserId(), newTransaction.getCategory(), newTransaction.getAmount());
                income.setDescription(newTransaction.getDescription());
                income.setTime(newTransaction.getTime());
                income.setDate(newTransaction.getDate());

                incomeService.updateIncome(id, income);
            }
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public User getUser(String mail, String password){
        if(!isValidMail(mail)) {
            throw new IllegalArgumentException(INVALID_MAIL_FORMAT_ERROR);
        }

        if(!isValidPassword(password)){
            throw new IllegalArgumentException(INVALID_PASSWORD_FORMAT_ERROR);
        }
        if(!isAlreadyRegistered(mail)){
            throw new UserNotFoundException("User not found.");
        }
        try{
            return userService.getUser(mail, password);
        }catch (SQLException ex){
            throw new UserNotFoundException("User not found.");
        }
    }

    public void updateUser(int userId, User newUser) {
        newUser.setUpdatedAt(new Timestamp(new java.util.Date().getTime()));
        try {
            userService.update(userId, newUser);
            loggedInUser.setUsername(newUser.getUsername());
            loggedInUser.setEmail(newUser.getEmail());
            loggedInUser.setPassword(newUser.getPassword());
            loggedInUser.setUpdatedAt(newUser.getUpdatedAt());
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public int getUserId(String mail) {
        try {
            return userService.getUserId(mail);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void registerReferral(int referralId) {
        try {
            userService.registerReferral(loggedInUser.getUserId(), referralId);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public List<User> getReferralUsers() {
        try {
            referral_users.clear();
            referral_users.addAll(userService.getReferralUsers(loggedInUser.getUserId()));
            return referral_users;
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void changeAccount(User referralUser) {
        try{
            userService.updateStatus(loggedInUser.getUserId(), "sleep");
        }catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
        loggedInUser = referralUser;
    }

    public void deleteAccount() {
        try {
            userService.deleteAccount(loggedInUser.getUserId());
            loggedInUser = null;
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public boolean isAlreadyReferred(int referralId) {
        try {
            return userService.isAlreadyReferred(loggedInUser.getUserId(), referralId);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void deleteExpenses() {
        try{
            expenseService.delete(loggedInUser.getUserId());
        }catch (SQLException ex){
            throw new UnableToWriteException("Unable to delete.");
        }
    }

    public void deleteIncomes() {
        try{
            incomeService.delete(loggedInUser.getUserId());
        }catch (SQLException ex){
            throw new UnableToWriteException("Unable to delete.");
        }
    }

    public void deleteTransactions() {
        try{
            transactionService.delete(loggedInUser.getUserId());
        }catch (SQLException ex){
            throw new UnableToWriteException("Unable to delete.");
        }
    }

    public void deleteBudget() {
        try{
            budgetService.delete(loggedInUser.getUserId());
        }catch (SQLException ex){
            throw new UnableToWriteException("Unable to delete.");
        }
    }

    public void updateStatus() {
        try{
            userService.updateStatus(loggedInUser.getUserId(), "sleep");
        }catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void createGroup(String groupName) {
        try {
            userService.createGroup(loggedInUser.getUserId(), groupName);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public void addMember(int userId, int groupId) {
        try{
            userService.addMember(userId, groupId);
        }catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public List<Group> getGroups() {
        try{
            return userService.getGroups(loggedInUser.getUserId());
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public void addGroupExpense(int groupId, int expenseId) {
        try{
            userService.addGroupExpenses(loggedInUser.getUserId(), groupId, expenseId);
        } catch (SQLException ex){
            throw new UnableToWriteException(WRITE_DATA_ERROR);
        }
    }

    public List<Expense> getGroupExpenses(int group_id) {
        try{
            return expenseService.getGroupExpenses(group_id);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public String getUsername(int userId) {
        try {
            return userService.getUsername(userId);
        } catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public List<String> getGroupUsername(int groupId) {
        try{
            return userService.getGroupUsername(groupId);
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }

    public boolean isAlreadyMember(int userId, int groupId) {

        try{
            return userService.isAlreadyMember(userId, groupId);
        }catch (SQLException ex){
            throw new UnableToReadException(READ_DATA_ERROR);
        }
    }
}


