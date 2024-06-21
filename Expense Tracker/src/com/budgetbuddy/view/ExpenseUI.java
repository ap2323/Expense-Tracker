package com.budgetbuddy.view;

import com.budgetbuddy.controller.UserController;
import com.budgetbuddy.models.Expense;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

class ExpenseUI {
    MainUI mainUI;
    UserController userController;
    public ExpenseUI(UserController userController, MainUI mainUI) {
        this.userController = userController;
        this.mainUI = mainUI;
    }

    void viewExpenseCategories(Scanner scanner) {
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
                viewExpenseCategories(scanner);
            }
        }

        userController.getExpensesByMonth(yearMonth);
        List<Expense> expensesByMonth = userController.getExpensesByMonth();

        if (expensesByMonth.isEmpty()) {
            System.out.println("No expenses found for " + yearMonth.getMonth() + " " + yearMonth.getYear());
        } else {
            System.out.println(); //new line
            System.out.printf("%-15s %-15s %n", yearMonth.getMonth(), yearMonth.getYear());

            viewCategoriesBar(userController.getExpensesByMonth());

            mainUI.togglePage(expensesByMonth);
        }
    }

    public void viewCategoriesBar(List<Expense> expenses) {
        float total = 0;
        Map<String, Float> categoryTotals = new HashMap<>();

        for (Expense entry : expenses) {
            String categoryName = entry.getCategory().getCategoryName();
            float amount = entry.getAmount();
            total += amount;
            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0f) + amount);
        }

        int maxUnits = 20;
        List<Map.Entry<String, Float>> sortedEntries = new ArrayList<>(categoryTotals.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        System.out.println();// new line

        for (Map.Entry<String, Float> entry : sortedEntries) {
            String category = entry.getKey();
            float amount = entry.getValue();
            int units = (int) ((amount / total) * maxUnits);
            StringBuilder progressBar = new StringBuilder();
            for (int i = 0; i < units; i++) {
                progressBar.append("🟦");
            }
            float percentage = (amount / total) * 100;
            System.out.printf("%-20s : %s %.2f%%\n", category, progressBar.toString(), percentage);
        }
    }
}
