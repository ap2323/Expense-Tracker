package com.budgetbuddy.view;

import com.budgetbuddy.controller.UserController;
import com.budgetbuddy.models.Category;
import com.budgetbuddy.models.CategoryType;
import com.budgetbuddy.models.Transaction;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

class TransactionUI {

    MainUI mainUI;

    UserController userController;
    public TransactionUI(UserController userController, MainUI mainUI) {
        this.userController = userController;
        this.mainUI = mainUI;
    }

    void addTransaction(Scanner scanner) {
        System.out.print("\nAmount(or Press Enter to back): ");
        String amount = scanner.nextLine();
        if (amount.trim().isEmpty()) {
            mainUI.showHomeMenu();
        }
        float transaction_amount = 0f;
        try {
            transaction_amount = Float.parseFloat(amount);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid amount format.");
            addTransaction(scanner);
        }

        mainUI.showCategories(); //to show available categories

        System.out.print("Enter category ID: ");
        String categoryInput = scanner.nextLine();
        int categoryId = 0;
        if (!categoryInput.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for category ID. Skipping category selection.");
                addTransaction(scanner);
            }
        } else {
            addTransaction(scanner);
        }
        String description;
        if(userController.getCategoryById(categoryId).getCategoryName().equals("Other Expenses")){
            System.out.print("Description : ");
            description = scanner.nextLine().trim();

            if(description.isEmpty()){
                System.out.println("\n Description must be entered for other expenses.");
                addTransaction(scanner);
                return;
            }

        } else {
            System.out.print("Description (optional): ");
            description = scanner.nextLine().trim();
            if (description.isEmpty()) description = "";
        }
        Date date = null;
        System.out.print("Enter new date (dd-MM-yyyy) (or press Enter to keep " + new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()) + "): ");
        String dateInput = scanner.nextLine();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (!dateInput.trim().isEmpty()) {

                date = new Date(sdf.parse(dateInput.trim()).getTime());

            } else {
                System.out.println("Keeping Current Date.");

                date = new Date(sdf.parse(sdf.format(new java.util.Date())).getTime());
            }

        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            addTransaction(scanner);
        }



        System.out.print("Enter new time (HH:mm) (or press Enter to keep " + new SimpleDateFormat("HH:mm").format(new java.util.Date()) + "): ");
        String timeInput = scanner.nextLine();

        Time time = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            if (!timeInput.trim().isEmpty()) {
                if (userController.isVaildTimeFormat(timeInput)) {

                    time = new Time(new SimpleDateFormat("HH:mm").parse(timeInput.trim()).getTime());

                } else {
                    System.out.println("Invalid time format. Please enter in HH:mm format (00:00 to 23:59).");
                    addTransaction(scanner);
                }
            } else {
                System.out.println("Keeping current Time.");
                time = new Time(simpleDateFormat.parse(simpleDateFormat.format(new java.util.Date())).getTime());
            }
        } catch(ParseException e){
            System.out.println("Invalid time format.");
            addTransaction(scanner);
        }
        userController.addTransaction(categoryId, transaction_amount, description, date, time);
    }

    void removeTransaction(Scanner scanner){
        showTransactions(scanner);

        System.out.println("\nEnter transaction id(or press Enter to back):");
        String id = scanner.nextLine();
        if (!id.trim().isEmpty()) {
            int transaction_id;
            Transaction removedTransaction = null;
            try {
                transaction_id = Integer.parseInt(id.trim());
                boolean isFound = false;
                Iterator<Transaction> iterator = userController.getTransactionsList().iterator();
                while (iterator.hasNext()){
                    Transaction transaction = iterator.next();
                    if(transaction.getId() == transaction_id){
                        isFound = true;
                        removedTransaction = new Transaction(transaction.getId(), transaction.getUserId(),transaction.getCategory(),transaction.getAmount());
                        removedTransaction.setDescription(transaction.getDescription());
                        removedTransaction.setDate(transaction.getDate());
                        removedTransaction.setTime(transaction.getTime());
                        iterator.remove();
                    }
                }

                if(isFound){
                    CategoryType categoryType= removedTransaction.getCategory().getCategoryType();

                    if(categoryType.equals(CategoryType.Income)){
                        int income_id = userController.getIncomeId(removedTransaction.getCategory().getCategoryId(), removedTransaction.getAmount(), removedTransaction.getDescription(), removedTransaction.getDate(), removedTransaction.getTime());
                        userController.removeIncome(income_id);
                    } else if(categoryType.equals(CategoryType.Expense)){
                        int expense_id = userController.getExpenseId(removedTransaction.getCategory().getCategoryId(), removedTransaction.getAmount(), removedTransaction.getDescription(), removedTransaction.getDate(), removedTransaction.getTime());
                        userController.removeExpense(expense_id);
                    }
                    userController.removeTransaction(transaction_id);
                } else {
                    System.out.println("Transaction not found!");
                    removeTransaction(scanner);
                }
            } catch (NumberFormatException e) {
                System.out.println("Id must be a number.");
                removeTransaction(scanner);
            }
        } else {
            mainUI.showHomeMenu();
        }
    }
    private void showTransactions(Scanner scanner) {
        System.out.print("\nEnter month and year (MM-YYYY)(or press Enter to use current MM-yyyy): ");
        String monthYearInput = scanner.nextLine().trim();
        YearMonth yearMonth = null;
        if(monthYearInput.trim().isEmpty()){
            yearMonth = YearMonth.now();
        } else {
            try {
                yearMonth = YearMonth.parse(monthYearInput, DateTimeFormatter.ofPattern("MM-yyyy"));

            } catch (DateTimeParseException e) {
                System.out.println("Invalid input format. Please enter MM-YYYY (e.g., 06-2024)");
                showTransactions(scanner); // Recursively call the method again for a retry
            }
        }

        // Get the list of transactions for the specified month and year
        userController.getTransactions(yearMonth);
        List<Transaction> transactionsList = userController.getTransactionsList();

        if (transactionsList.isEmpty()) {
            System.out.println("No transactions found for " + yearMonth.getMonth() + "\t" +yearMonth.getYear() + ".");
            return;
        }
        mainUI.togglePage(transactionsList);
    }

    void editTransaction(Scanner scanner){
        showTransactions(scanner);

        System.out.println("\nEnter transaction id(or press Enter to back):");
        String id = scanner.nextLine();;
        if(id.trim().isEmpty()){
            mainUI.showHomeMenu();
        }
        int transaction_id = 0;
        try{
            transaction_id = Integer.parseInt(id);
        }catch (NumberFormatException ex){
            System.out.println("Invalid id format.");
            editTransaction(scanner);
        }

        Transaction oldTransaction = null;
        Transaction newTransaction = null;

        for(Transaction transaction : userController.getTransactionsList()){
            if(transaction.getId() == transaction_id){
                newTransaction = new Transaction(transaction.getId(),transaction.getUserId(), transaction.getCategory(), transaction.getAmount());
                newTransaction.setDescription(transaction.getDescription());
                newTransaction.setDate(transaction.getDate());
                newTransaction.setTime(transaction.getTime());

                oldTransaction = transaction;
            }
        }

        if(newTransaction != null){
            System.out.print("Enter new amount (or press Enter to keep " + newTransaction.getAmount() + "): ");
            String amountInput = scanner.nextLine();
            if (!amountInput.trim().isEmpty()) {
                try {
                    float amount = Float.parseFloat(amountInput.trim());
                    newTransaction.setAmount(amount);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount");
                    editTransaction(scanner);
                }
            } else {
                System.out.println("Keeping original amount.");
            }

            mainUI.showCategories();

            System.out.print("Enter new category ID (or press Enter to keep " + newTransaction.getCategory().getCategoryId() + "): ");
            String categoryInput = scanner.nextLine();

            if (categoryInput.trim().isEmpty()) {
                newTransaction.setCategory(oldTransaction.getCategory());
            } else {
                try {
                    int newCategoryId = Integer.parseInt(categoryInput.trim());
                    Category newCategory = userController.getCategoryById(newCategoryId);
                    if (newCategory != null) {
                        newTransaction.setCategory(newCategory);

                    } else {
                        System.out.println("Category not found");
                        editTransaction(scanner);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input for category ID.");
                    editTransaction(scanner);
                }
            }

            System.out.print("Enter new description (or press Enter to keep \"" + newTransaction.getDescription() + "\"): ");
            String descriptionInput = scanner.nextLine();
            if (!descriptionInput.trim().isEmpty()) {
                newTransaction.setDescription(descriptionInput.trim());
            } else {
                System.out.println("Keeping original description.");
            }
            System.out.print("Enter new date (dd-MM-yyyy) (or press Enter to keep " + new SimpleDateFormat("dd-MM-yyyy").format(newTransaction.getDate()) + "): ");
            String dateInput = scanner.nextLine();
            if (!dateInput.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date newDate = new Date(sdf.parse(dateInput.trim()).getTime());
                    newTransaction.setDate(newDate);
                } catch (ParseException e) {
                    System.out.println("Invalid date format.");
                    editTransaction(scanner);
                }
            } else {
                System.out.println("Keeping original Date.");
            }

            System.out.print("Enter new time (HH:mm) (or press Enter to keep " + newTransaction.getTime() + "): ");
            String timeInput = scanner.nextLine();

            if (!timeInput.trim().isEmpty()) {
                if(userController.isVaildTimeFormat(timeInput)) {
                    try {
                        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                        Time newTime = new Time(sdfTime.parse(timeInput.trim()).getTime());
                        newTransaction.setTime(newTime);
                    } catch (ParseException e) {
                        System.out.println("Invalid time format.");
                        editTransaction(scanner);
                    }
                } else {
                    System.out.println("Invalid time format. Please enter in HH:mm format (00:00 to 23:59).");
                    editTransaction(scanner);
                }
            } else {
                System.out.println("Keeping original Time.");
            }

            CategoryType newType = newTransaction.getCategory().getCategoryType();
            CategoryType oldType = oldTransaction.getCategory().getCategoryType();

            int income_id = userController.getIncomeId(oldTransaction.getCategory().getCategoryId(), oldTransaction.getAmount(), oldTransaction.getDescription(), oldTransaction.getDate(), oldTransaction.getTime());
            int expense_id = userController.getExpenseId(oldTransaction.getCategory().getCategoryId(), oldTransaction.getAmount(), oldTransaction.getDescription(), oldTransaction.getDate(), oldTransaction.getTime());

            if (newType.equals(CategoryType.Income) && oldType.equals(CategoryType.Expense)) {
                userController.removeExpense(income_id);
                userController.addIncome(newTransaction.getCategory(), newTransaction.getAmount(), newTransaction.getDescription(), newTransaction.getTime(), newTransaction.getDate());
            } else if(newType.equals(CategoryType.Expense) && oldType.equals(CategoryType.Income)) {
                userController.removeIncome(expense_id);
                userController.addExpense(newTransaction.getCategory(), newTransaction.getAmount(), newTransaction.getDescription(), newTransaction.getTime(), newTransaction.getDate());
            } else if(newType.equals(CategoryType.Income) && oldType.equals(CategoryType.Income)) {
                userController.update(income_id, newTransaction);
            } else if(newType.equals(CategoryType.Expense) && oldType.equals(CategoryType.Expense)){
                userController.update(expense_id, newTransaction);
            }
            userController.updateTransaction(newTransaction, oldTransaction);
            System.out.println("Updated Successfully!.");
        } else {
            System.out.println("No Transaction Found!");
        }
    }

}
