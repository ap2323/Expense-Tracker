package com.budgetbuddy.view;

import com.budgetbuddy.controller.UserController;
import com.budgetbuddy.exceptions.UnableToReadException;
import com.budgetbuddy.exceptions.UnableToWriteException;
import com.budgetbuddy.exceptions.UserAlreadyFoundException;
import com.budgetbuddy.exceptions.UserNotFoundException;
import com.budgetbuddy.models.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MainUI {
    private final UserController userController;
    private static final Scanner scanner = new Scanner(System.in);

    private final UserUI userUI;
    private final TransactionUI transactionUI;
    private final ExpenseUI expenseUI;

    public MainUI(UserController userController) {
        this.userController = userController;
        this.userUI = new UserUI(userController, this);
        this.transactionUI = new TransactionUI(userController, this);
        this.expenseUI = new ExpenseUI(userController, this);
    }

    public MainUI(){
        this.userController = null;
        this.expenseUI = null;
        this.userUI = null;
        this.transactionUI = null;
    }

    public void showLoginMenu() {
        while(true) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("\nEnter option:");
            String choice = scanner.nextLine().trim();

            if(choice.trim().isEmpty()){
                showLoginMenu();
            }
            int option = 0;
            try {
                option = Integer.parseInt(choice);
            }catch (NumberFormatException ex){
                System.out.println("Option must be a number.");
                showLoginMenu();
            }

            switch (option) {
                case 1:
                    try {
                        login();
                        showHomeMenu();
                    } catch (UnableToReadException ex) {
                        System.out.println(ex.getMessage());
                    } catch (IllegalArgumentException exception) {
                        System.out.println(exception.getMessage());
                        showInvalidMailOrPassword();
                    } catch (UserNotFoundException | UserAlreadyFoundException exception) {
                        System.out.println(exception.getMessage());
                        showLoginFailed();
                    }
                    break;
                case 2:
                    try {
                        register();
                        System.out.println("\nRegister Successfully.");
                    } catch (IllegalArgumentException exception) {
                        System.out.println(exception.getMessage());
                        showInvalidMailOrPassword();
                    } catch (UnableToWriteException | UserAlreadyFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 3:
                    userController.updateStatus();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    showLoginMenu();
            }
        }
    }

    private void login() {

        System.out.print("Email(or press Enter to back): ");
        String email = scanner.nextLine().trim().toLowerCase();
        if(email.isEmpty()){
            showLoginMenu();
        }
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if(password.isEmpty()){
            System.out.println("All fields are must be entered.");
            login();
        }

        userController.login(email, password);
    }

    private void register() {
        System.out.print("Username(or press Enter to back): ");
        String username = scanner.nextLine().trim().toLowerCase();
        if(username.isEmpty()){
            showLoginMenu();
        }
        System.out.print("Email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if(email.isEmpty() || password.isEmpty()){
            System.out.println("All fields are must be entered.");
            register();
        }

        if(userController.isValidUsername(username)){
            System.out.println("Username is already taken.");
            register();
        }
        userController.register(username, email, password);
    }
    private String getFormattedString(String string){
        return String.format("%-" + 23 +"s", string);
    }
    // Helper method to generate the progress bar
    private String generateProgressBar(double totalExpenses, double budget) {
        StringBuilder progressBar = new StringBuilder();
        int totalUnits = 20; // Total units in the progress bar

        if (budget == 0) {
            // Budget is zero, fill the progress bar with white rounds
            for (int i = 0; i < totalUnits; i++) {
                progressBar.append("⬜\uFE0F"); //white square
            }
        } else {
            int greenUnits = (int) ((totalExpenses / budget) * totalUnits);
            int redUnits = 0;

            if (totalExpenses > budget) {
                redUnits = (int) (((totalExpenses - budget) / budget) * totalUnits);
            }

            for (int i = 0; i < totalUnits; i++) {
                if (i < redUnits) {
                    progressBar.append("\uD83D\uDFE5"); //red square
                } else if (i < greenUnits) {
                    progressBar.append("\uD83D\uDFE9"); // green square
                } else {
                    progressBar.append("⬜\uFE0F"); //white square
                }
            }
        }
        return progressBar.toString();
    }

    private int calculateSafeToSpend(double totalExpenses, double budget) {
        if (budget == 0 || totalExpenses >= budget) {
            return 0;
        }

        double remainingBudget = budget - totalExpenses;
        LocalDate now = LocalDate.now();
        int currentDay = now.getDayOfMonth();
        int daysInMonth = YearMonth.from(now).lengthOfMonth();
        int daysRemaining = daysInMonth - currentDay;

        return (int) remainingBudget / (daysRemaining + 1); // +1 to include the current day
    }

    void showHomeMenu() {

        float totalExpenses = 0f;
        float budget = 0f;

        List<Transaction> recentTransactions = new ArrayList<>();
        while (true) {
            System.out.printf("\nHi, %-23s %-29s %-20s\n", userController.getLoggedInUser().getUsername(), "BUDGET BUDDY" ,"1. Settings");

            try {
                totalExpenses = userController.getTotalExpenses(YearMonth.now()); // get current month and year total income
                budget = userController.getUserBudget();
            } catch (UnableToReadException ex){
                System.out.println(ex.getMessage());
            }

            // Calculate percentage spent
            int percentageSpent = budget > 0 ? (int) ((totalExpenses / budget) * 100) : 0;
            String progressBar = generateProgressBar(totalExpenses, budget);
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.printf("%s\t%d%%\n", progressBar, percentageSpent);
            System.out.printf("\n%-24s %s\n",
                    getFormattedString("Spend: " + totalExpenses),
                    (budget > 0.0f) ? getFormattedString("Budget: " + budget) + "Safe to spend: " + calculateSafeToSpend(totalExpenses, budget) + "/day" : ""
            );
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.println("\n2. View Income\t3. Set Monthly Budget\t4. View Expenses");
            System.out.printf("%-26s %-12s %n", "\nRecent Transactions", "5. Add Transaction");
            System.out.println("──────────────────────────────────────────────");
            try {
                recentTransactions.clear();
                recentTransactions = userController.getRecentTransactions(5);
            } catch (UnableToReadException ex){
                System.out.println(ex.getMessage());
            }
            if (!recentTransactions.isEmpty()) {
                for (Transaction transaction : recentTransactions) {
                    System.out.println(transaction);
                    System.out.println("──────────────────────────────────────────────");
                }
            } else {
                System.out.println("No recent transactions.");
                System.out.println("──────────────────────────────────────────────");
            }

            System.out.println("6. Remove Transaction");
            System.out.println("7. Edit Transaction");
            System.out.println("8. Create Group");
            System.out.println("9. View Group");
            System.out.println("10. Exit");
            System.out.println("──────────────────────────────────────────────");
            System.out.print("Enter Choice: ");
            String choice = scanner.nextLine().trim();

            if(choice.isEmpty()){
                showHomeMenu();
            }

            int option = Integer.parseInt(choice);
            switch (option) {
                case 1:
                    viewSettings();
                case 2:
                    try {
                        viewIncomeCategories();
                    } catch (UnableToReadException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 3:
                    try {
                        setMonthlyBudget();
                        showHomeMenu();
                    } catch (UnableToWriteException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 4:
                    try {
                        expenseUI.viewExpenseCategories(scanner);
                    } catch (UnableToReadException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 5:
                    try {
                        transactionUI.addTransaction(scanner);
                        showHomeMenu();
                    } catch (UnableToWriteException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 6:
                    try {
                        transactionUI.removeTransaction(scanner);
                    } catch (UnableToWriteException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 7:
                    try {
                        transactionUI.editTransaction(scanner);
                    } catch (UnableToReadException | UnableToWriteException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 8:
                    try {
                        userUI.createGroup(scanner);
                    } catch (UnableToWriteException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 9:
                    try {
                        userUI.viewGroup(scanner);
                    } catch (UnableToReadException | UnableToWriteException ex){
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 10:
                    userController.updateStatus();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    void viewSettings() {
        while(true) {
            System.out.println("\n─────────────────────────");
            System.out.printf("%-24s| %-25s| %-25s| %-25s |%n", "| 1. Edit Profile", "\n| 2. Change Account", "\n| 3. Add Account", "\n| 4. Delete Account ");
            System.out.println("─────────────────────────");

            System.out.println("\nEnter option(or Press Enter to back):");
            String option = scanner.nextLine();
            if (option.trim().isEmpty()) {
                showHomeMenu();
            } else {
                int choice = 0;
                try {
                    choice = Integer.parseInt(option);

                }catch (NumberFormatException ex){
                    System.out.println("Option must be a number.");
                    viewSettings();
                }

                switch (choice) {
                    case 1:
                        try {
                            userUI.editProfile(scanner);
                            System.out.println("\nSuccessfully Updated.");
                        } catch (UnableToReadException | UnableToWriteException ex){
                            System.out.println(ex.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            userUI.changeAccount(scanner);
                            showHomeMenu();
                        } catch (UnableToReadException ex){
                            System.out.println(ex.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            userUI.addAccount(scanner);
                        } catch (UnableToReadException | UnableToWriteException | UserAlreadyFoundException  ex){
                            System.out.println(ex.getMessage());
                        } catch (IllegalArgumentException ex){
                            System.out.println(ex.getMessage());
                            showInvalidMailOrPassword();
                        }
                        break;
                    case 4:
                        try{
                            userUI.deleteAccount(scanner);
                        } catch (UnableToWriteException ex){
                            System.out.println(ex.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Invalid Option.");
                }
            }
        }
    }

    private  void setMonthlyBudget() {
        System.out.print("\nEnter amount(or press Enter to back): ");
        String amount = scanner.nextLine();

        if(amount.trim().isEmpty()){
            showHomeMenu();
        }
        float budget_amount = 0f;
        try{
            budget_amount = Float.parseFloat(amount);
        } catch (NumberFormatException ex){
            System.out.println("Invalid amount format.");
            setMonthlyBudget();
        }

        userController.setBudget(budget_amount);
        System.out.println("Budget Allotted!");
    }

    void togglePage(List<?> list) {

        int currentPage = 1;
        int transactionsPerPage = 10;
        boolean keepShowing = true;

        while (keepShowing) {
            int startIndex = (currentPage - 1) * transactionsPerPage;
            int endIndex = Math.min(startIndex + transactionsPerPage, list.size());

            List<?> currentItems = list.subList(startIndex, endIndex);


            if(currentItems.get(0) instanceof Transaction) {
                System.out.println("\n──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                System.out.printf("| %-16s  %-22s  %-25s  %-22s  %-23s |%n", "Transaction ID", "Date", "Category", "Amount", "Description");
                System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
            } else if (currentItems.get(0) instanceof Expense) {
                System.out.println("\n──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                System.out.printf("| %-16s  %-22s  %-25s  %-22s  %-23s |%n", "Expense ID", "Date", "Category", "Amount", "Description");
                System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
            } else if (currentItems.get(0) instanceof Income) {
                System.out.println("\n──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                System.out.printf("| %-16s  %-22s  %-25s  %-22s  %-23s |%n", "Income ID", "Date", "Category", "Amount", "Description");
                System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
            } else if (currentItems.get(0) instanceof User) {
                System.out.println("\n────────────────────────────────────────────────");
                System.out.printf("| %-18s %-25s |%n", "Username", "Created At");
                System.out.println("────────────────────────────────────────────────");
            }


            for (Object item : currentItems) {
                if (item instanceof Expense) {
                    Expense expense = (Expense) item;
                    System.out.printf("| %-16d  %-20s  %-25s  %-22.2f  %-23s |%n",
                            expense.getId(), expense.getDate().toString(), expense.getCategory().getCategoryName(),
                            expense.getAmount(), expense.getDescription());
                } else if (item instanceof Income) {
                    Income income = (Income) item;
                    System.out.printf("| %-16d  %-20s  %-25s  %-22.2f  %-23s |%n",
                            income.getId(), income.getDate().toString(), income.getCategory().getCategoryName(),
                            income.getAmount(), income.getDescription());
                } else if (item instanceof Transaction) {

                    Transaction transaction = (Transaction) item;
                    System.out.printf("| %-16d  %-20s  %-25s  %-22.2f  %-23s |%n",
                            transaction.getId(), transaction.getDate().toString(), transaction.getCategory().getCategoryName(),
                            transaction.getAmount(), transaction.getDescription());
                } else if (item instanceof User) {
                    User user = (User) item;
                    System.out.printf("| %-18s %-25s |%n", user.getUsername(), user.getCreatedAt());
                }

                if(currentItems.get(0) instanceof Transaction) {
                    System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                } else if (currentItems.get(0) instanceof Expense) {

                    System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                } else if (currentItems.get(0) instanceof Income) {

                    System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                } else if (currentItems.get(0) instanceof User) {
                    System.out.println("────────────────────────────────────────────────");
                }

            }

            System.out.println("Page " + currentPage + " of " + ((list.size() + transactionsPerPage - 1) / transactionsPerPage));
            System.out.println("[N]ext page | [P]revious page | [B]ack");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();
            char option = 0;
            if (choice.trim().isEmpty()) {
                showHomeMenu();
            } else {
                option = choice.trim().toUpperCase().charAt(0);
            }
            switch (option) {
                case 'N':
                    if (currentPage * transactionsPerPage < list.size()) {
                        currentPage++;
                    } else {
                        System.out.println("No more records.");
                    }
                    break;
                case 'P':
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("No more records.");
                    }
                    break;
                case 'B':
                    keepShowing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
    void showCategories() {
        List<Category> categories = userController.getCategories();
        for (Category category : categories){
            System.out.println(category);
        }
    }

    private void viewIncomeCategories() {

        System.out.print("Enter month and year (MM-YYYY)(or press Enter to use current MM-yyyy): ");
        String monthYearInput = scanner.nextLine();
        YearMonth yearMonth = null;
        if(monthYearInput.trim().isEmpty()){
            yearMonth = YearMonth.now();
        }else {

            try {
                yearMonth = YearMonth.parse(monthYearInput, DateTimeFormatter.ofPattern("MM-yyyy"));

            } catch (DateTimeParseException e) {
                System.out.println("Invalid input format. Please enter MM-YYYY (e.g., 06-2024)");
                viewIncomeCategories();
            }
        }
        userController.getIncomesByMonth(yearMonth);
        List<Income> incomesByMonth = userController.getIncomesByMonth();
        System.out.println("\nTotal Income: " + userController.getTotalIncome(yearMonth));

        if (incomesByMonth.isEmpty()) {
            System.out.println("No incomes found for " + yearMonth.getMonth() + " " + yearMonth.getYear());
        } else {
            System.out.println(); //new line
            System.out.printf("%-15s %-15s %n", yearMonth.getMonth(), yearMonth.getYear());
            togglePage(incomesByMonth);
        }

    }

    void showLoginFailed() {
        System.out.println("Login Failed.");
    }

    public void showTransactionSuccess() {
        System.out.println("Transaction added successfully.");
    }


    void showInvalidMailOrPassword(){
        System.out.println("\nMail Format (exampleuser@example.com");
        System.out.println("\nPassword must between 8 to 15 characters.");
        System.out.println("Password does not contain any spaces.");
        System.out.println("Password must contain at least 1 digit [0-9].");
        System.out.println("Password must contain at least 1 Capital letter [A-Z].");
        System.out.println("Password must contain at least 1 small letter [a-z].");
        System.out.println("Password must contain any special characters [!, @, #, $..]");
    }
}
