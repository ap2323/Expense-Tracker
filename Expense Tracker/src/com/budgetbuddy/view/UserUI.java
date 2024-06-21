package com.budgetbuddy.view;

import com.budgetbuddy.controller.UserController;
import com.budgetbuddy.exceptions.UserAlreadyFoundException;
import com.budgetbuddy.exceptions.UserNotFoundException;
import com.budgetbuddy.models.*;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class UserUI {
    private UserController userController;
    private MainUI mainUI;
    private ExpenseUI expenseUI;
    private List<Group> groups = new ArrayList<>();
    public UserUI(UserController userController, MainUI mainUI) {
        this.userController = userController;
        this.mainUI = mainUI;

        this.expenseUI = new ExpenseUI(userController, mainUI);
    }

    void addAccount(Scanner scanner) {
        System.out.println("\n1. Add Existing Account");
        System.out.println("2. Create new Account");
        System.out.println("\nEnter option(or press Enter to back):");
        String choice = scanner.nextLine().trim();

        if(choice.isEmpty()){
            mainUI.viewSettings();
        }
        int option = 0;
        try{
            option = Integer.parseInt(choice);
        } catch (NumberFormatException ex){
            System.out.println("\nOption must be a number.");
            addAccount(scanner);
        }
        switch (option){
            case 1:
                addExistingAccount(scanner);
                break;
            case 2:
                createNewAccount(scanner);
                break;
            default:
                System.out.println("Invalid Option.");
                addAccount(scanner);
        }
    }

    void addExistingAccount(Scanner scanner){
        System.out.print("Email(or press Enter to back): ");
        String email = scanner.nextLine().trim().toLowerCase();
        if(email.isEmpty()){
            addAccount(scanner);
        }
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if(password.isEmpty()){
            System.out.println("\nAll fields are must be entered.");
            addExistingAccount(scanner);
        }
        User user = null;
        try{
            user = userController.getUser(email, password);
            System.out.println(user);

        } catch (IllegalArgumentException  ex){

            System.out.println(ex.getMessage());
            mainUI.showInvalidMailOrPassword();
            addExistingAccount(scanner);

        } catch (UserNotFoundException ex){

            System.out.println(ex.getMessage());
            addExistingAccount(scanner);
        }

        if(user.getStatus().equals("removed")){
            System.out.println("User Not found.");
            addExistingAccount(scanner);
        }
        int referral_id = userController.getUserId(email);
        if(userController.isAlreadyReferred(referral_id)){
            System.out.println("\nAlready Referred.");
            addExistingAccount(scanner);
        }
        userController.registerReferral(referral_id);

        System.out.println("\nAdded Successfully!.");
    }

    private void showAccounts(){

        List<User> referral_users = userController.getReferralUsers();

        System.out.println("\n────────────────────────────────────────────────────────");
        if(referral_users.isEmpty()){
            System.out.println("No Accounts.");
        } else {
            System.out.printf("| %-13s %-22s %-15s |", "User ID", "Username", "Status");
            System.out.println("\n────────────────────────────────────────────────────────");
            for (User user : referral_users) {
                System.out.printf("| %-13s  %-20s  %-15s | %n", user.getUserId(), user.getUsername(), user.getStatus());
            }
        }
        System.out.println("────────────────────────────────────────────────────────");
    }
    void createNewAccount(Scanner scanner) {
        System.out.print("\nUsername(or press Enter to back): ");
        String username = scanner.nextLine().trim().toLowerCase();
        if (username.isEmpty()) {
            addAccount(scanner);
        }
        System.out.print("Email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("\nAll fields are must be entered.");
            createNewAccount(scanner);
        }

        if (userController.isValidUsername(username)) {
            System.out.println("\nUsername is already taken.");
            createNewAccount(scanner);
        }
        if (userController.getLoggedInUser().getEmail().equals(email)) {
            System.out.println("\nCurrent user cannot be referral.");
            createNewAccount(scanner);
        }
        try {
            userController.register(username, email, password);
        }catch (UserAlreadyFoundException ex){
            System.out.println(ex.getMessage());
            createNewAccount(scanner);
        }

        int referral_id = userController.getUserId(email);
        userController.registerReferral(referral_id);

        System.out.println("\nAdded Successfully!.");
    }

    void changeAccount(Scanner scanner){
        showAccounts();
        List<User> referral_users = userController.getReferralUsers();
        if(referral_users.isEmpty()){
            mainUI.viewSettings();
        }
        System.out.println("\nEnter user id(or press enter to back):");
        String id = scanner.nextLine().trim();

        if(id.isEmpty()){
            mainUI.viewSettings();
        }
        int user_id=0;
        try{
            user_id = Integer.parseInt(id);
        }catch (NumberFormatException ex){
            System.out.println("ID must be a number.");
            changeAccount(scanner);
        }

        User referralUser = null;
        for (User user : referral_users){
            if(user.getUserId() == user_id && user.getStatus().equals("sleep")){
                referralUser = user;
                userController.changeAccount(referralUser);
            } else if(user.getUserId() == user_id && user.getStatus().equals("active")){
                System.out.println("\nAlready in use.");
                changeAccount(scanner);
            } else if (user.getUserId() == user_id && user.getStatus().equals("removed")){
                System.out.println("\nCannot be connect.");
                changeAccount(scanner);
            }
        }

        if(referralUser == null){
            System.out.println("User Not Found.");
            changeAccount(scanner);
        }
    }
    void editProfile(Scanner scanner) {
        User oldUser = userController.getLoggedInUser();
        showProfile(oldUser);

        User newUser = new User(oldUser.getUsername(),oldUser.getEmail(), oldUser.getPassword());
        newUser.setUpdatedAt(oldUser.getUpdatedAt());
        newUser.setCreatedAt(oldUser.getCreatedAt());

        System.out.println("\nEnter new username (or Press enter to back):");
        String newName = scanner.nextLine().trim().toLowerCase();

        if(newName.isEmpty()){
            mainUI.viewSettings();
        } else {
            newUser.setUsername(newName);
        }

        if(!newUser.getUsername().equals(oldUser.getUsername())) {
            if (userController.isValidUsername(newUser.getUsername())) {
                System.out.println("Username is already taken.");
                editProfile(scanner);
            }
        }

        System.out.println("Enter new mail(or press enter to keep current mail):");
        String newMail = scanner.nextLine().trim().toLowerCase();

        if(newMail.isEmpty()){
            newUser.setEmail(oldUser.getEmail());
        } else {
            newUser.setEmail(newMail);
        }
        System.out.println("Enter new password(or press enter to keep current password):");
        String newPassword = scanner.nextLine().trim().toLowerCase();

        if(newPassword.isEmpty()){
            newUser.setPassword(oldUser.getPassword());
        } else {
            newUser.setPassword(newPassword);
        }
        if(!userController.isValidMail(newUser.getEmail()) || !userController.isValidPassword(newUser.getPassword())){
            System.out.println("Invalid mail or password");
            mainUI.showInvalidMailOrPassword();
            editProfile(scanner);
        }
        if(userController.isAlreadyRegistered(newUser.getEmail()) && !newUser.getEmail().equals(userController.getLoggedInUser().getEmail())) {
            System.out.println("User Already Registered.");
            editProfile(scanner);
        }
        if(newUser.getUsername().equals(oldUser.getUsername()) && newUser.getEmail().equals(oldUser.getEmail()) && newUser.getPassword().equals(oldUser.getPassword())){
            System.out.println("Nothing Changed.");
            mainUI.viewSettings();
        }
        userController.updateUser(oldUser.getUserId(), newUser);
    }

    private void showProfile(User user) {

        String maskedMail = userController.maskEmail(user.getEmail());
        String maskedPassword = userController.maskPassword(user.getPassword());
        if(user.getUpdatedAt() == null){
            System.out.println(" ");
        } else {
            long timestamp = user.getUpdatedAt().getTime();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(timestamp);
            String time = new SimpleDateFormat("HH:mm").format(timestamp);
            System.out.printf("%n %-15s %-10s %-10s %n" , "Last modified at: ", date, time);
        }
        System.out.println("Username: " + user.getUsername());
        System.out.println("Mail: " + maskedMail);
        System.out.println("Password: " + maskedPassword);
    }

    void deleteAccount(Scanner scanner) {
        System.out.println("\nAll Data will be erased. Are You Sure to Delete?('Y' to delete or Press Enter to back):");
        String confirmation = scanner.nextLine().trim();

        if(confirmation.isEmpty()){
            mainUI.viewSettings();
        }

        char ch = confirmation.toLowerCase().charAt(0);
        if(ch == 'y') {
            userController.deleteBudget();
            userController.deleteExpenses();
            userController.deleteIncomes();
            userController.deleteTransactions();
            userController.deleteAccount();
            System.out.println("\nDeleted Successfully.");
            mainUI.showLoginMenu();
        } else {
            mainUI.viewSettings();
        }
    }

    void createGroup(Scanner scanner) {
        System.out.println("\nEnter group name (or press Enter to back):");
        String groupName = scanner.nextLine().trim();

        if(groupName.isEmpty()){
            mainUI.showHomeMenu();
        }

        userController.createGroup(groupName);
    }

    private void showGroups(){
        groups.clear();
        groups = userController.getGroups();
        if(groups.isEmpty()) {
            System.out.println("No groups.");
            mainUI.showHomeMenu();
        }
        for (Group group : groups){
            System.out.println(group.getGroup_id() + "\t" + group.getGroup_name());
        }
    }
    public void viewGroup(Scanner scanner) {
        showGroups();
        System.out.println("\nEnter group id(or press Enter to back):");
        String id = scanner.nextLine().trim();

        if(id.isEmpty()){
            mainUI.showHomeMenu();
        }
        int group_id = 0;
        try{
            group_id = Integer.parseInt(id);
        }catch (NumberFormatException ex){
            System.out.println("Id must be a number.");
            viewGroup(scanner);
        }
        Group selected_group = null;
        for (Group group : groups){
            if(group.getGroup_id() == group_id){
                selected_group = group;
            }
        }

        if(selected_group == null){
            System.out.println("\nGroup not found.");
        } else {
            viewGroupOptions(scanner, selected_group.getGroup_id());
        }

    }

    private void viewGroupOptions(Scanner scanner, int groupId){
        while (true) {
            System.out.println("\n1. Add Members");
            System.out.println("2. Add Expense");
            System.out.println("3. View Expense");
            System.out.println("4. View Members");
            System.out.println("\nEnter Choice(or press Enter to back):");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                viewGroup(scanner);
            }
            int option = 0;
            try {
                option = Integer.parseInt(choice);
            } catch (NumberFormatException ex) {
                System.out.println("Option must be a Number.");
                viewGroupOptions(scanner, groupId);
            }

            switch (option) {
                case 1:
                    addMembers(scanner, groupId);
                    break;
                case 2:
                    addExpense(scanner, groupId);
                    break;
                case 3:
                    viewExpenses(scanner, groupId);
                    break;
                case 4:
                    viewMembers(scanner, groupId);
                    break;
                default:
                    System.out.println("\nInvalid option.");
            }
        }
    }

    private void viewMembers(Scanner scanner, int groupId) {

        List<String> users = userController.getGroupUsername(groupId);

        System.out.println("\n────────────────────────────");
        System.out.printf("| %-25s | %n", "MEMBERS");
        System.out.println("─────────────────────────────");
        for (String names : users){
            if(names.equals(userController.getLoggedInUser().getUsername())){
                System.out.printf("| %-25s | %n" ,"You");
            } else {
                System.out.printf("| %-25s | %n", names);
            }
        }
        System.out.println("─────────────────────────────");

        System.out.println("\nPress any key to to back:");
        String enter = scanner.nextLine().trim();

        if(enter.isEmpty()){
            viewGroupOptions(scanner, groupId);
        }

    }

    private void addMembers(Scanner scanner, int groupId) {
        System.out.println("\nEnter mail(or press enter to back):");
        String mail = scanner.nextLine().trim().toLowerCase();

        if(mail.isEmpty()){
            viewGroup(scanner);
        }

        if(userController.isAlreadyRegistered(mail)){
            if(userController.isValidMail(mail)) {
                int userId = userController.getUserId(mail);
                if(!userController.isAlreadyMember(userId, groupId)){
                    userController.addMember(userId, groupId);
                } else {
                    System.out.println("\n Already added.");
                    addMembers(scanner, groupId);
                }

            } else {
                System.out.println("Invalid mail format.");
                addMembers(scanner,groupId);
            }
        } else {
            System.out.println("\nUser not found.");
            addMembers(scanner,groupId);
        }
    }

    private void addExpense(Scanner scanner, int groupId){
        System.out.print("\nAmount(or Press Enter to back): ");
        String amount = scanner.nextLine();
        if (amount.trim().isEmpty()) {
            mainUI.showHomeMenu();
        }
        float expense_amount = 0f;
        try {
            expense_amount = Float.parseFloat(amount);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid amount format.");
            addExpense(scanner, groupId);
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
                addExpense(scanner, groupId);
            }
        } else {
            addExpense(scanner,groupId);
        }

        System.out.print("Description (optional): ");
        String description = scanner.nextLine().trim();
        if (description == null) description = "";

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
            addExpense(scanner,groupId);
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
                    addExpense(scanner,groupId);
                }
            } else {
                System.out.println("Keeping current Time.");
                time = new Time(simpleDateFormat.parse(simpleDateFormat.format(new java.util.Date())).getTime());
            }
        } catch(ParseException e){
            System.out.println("Invalid time format.");
            addExpense(scanner,groupId);
        }
        userController.addTransaction(categoryId, expense_amount, description, date, time);
       int expense_id = userController.getExpenseId(categoryId, expense_amount, description, date, time);

       userController.addGroupExpense(groupId, expense_id);
    }

    private void viewExpenses(Scanner scanner, int groupId) {

       List<Expense> expenseList = userController.getGroupExpenses(groupId);

       List<String> usernames = new ArrayList<>();
       float totalExpenses = 0f;
       if(!expenseList.isEmpty()) {
           for (Expense expense : expenseList) {
               usernames.add(userController.getUsername(expense.getUserId()));
               totalExpenses += expense.getAmount();
           }
       } else {
           System.out.println("\nNo expenses.");
       }

       System.out.println("\nTotal Expenses: " + totalExpenses);

       togglePage(expenseList, usernames, scanner, groupId);

    }

    private void viewUserExpenseBar(List<Expense> expenses, List<String> usernames) {
        float total = 0;
        Map<String, Float> userTotals = new HashMap<>();

        int index = 0;
        for (Expense entry : expenses) {
            String username = usernames.get(index++);
            float amount = entry.getAmount();
            total += amount;
            userTotals.put(username, userTotals.getOrDefault(username, 0f) + amount);
        }

        int maxUnits = 20;
        List<Map.Entry<String, Float>> sortedEntries = new ArrayList<>(userTotals.entrySet());
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


    private void togglePage(List<Expense> list, List<String> usernames, Scanner scanner, int groupId) {
        expenseUI.viewCategoriesBar(list);
        viewUserExpenseBar(list, usernames);
        int currentPage = 1;
        int transactionsPerPage = 10;
        boolean keepShowing = true;
        while (keepShowing) {
            int i=0;
            int startIndex = (currentPage - 1) * transactionsPerPage;
            int endIndex = Math.min(startIndex + transactionsPerPage, list.size());

            List<Expense> currentItems = list.subList(startIndex, endIndex);
            List<String> currentUsers = usernames.subList(startIndex, endIndex);

                System.out.println("\n──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                System.out.printf("| %-25s  %-22s  %-16s  %-22s  %-23s |%n", "Date", "Username", "Category", "Amount", "Description");
                System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");

            for (Expense expense : currentItems) {
                    System.out.printf("|  %-25s  %-20s  %-16s  %-22.2f  %-23s |%n",
                            expense.getDate().toString(), currentUsers.get(i), expense.getCategory().getCategoryName(),
                            expense.getAmount(), expense.getDescription());
                    System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
                i++;
            }

            System.out.println("Page " + currentPage + " of " + ((list.size() + transactionsPerPage - 1) / transactionsPerPage));
            System.out.println("[N]ext page | [P]revious page | [B]ack");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();
            char option = 0;
            if (choice.trim().isEmpty()) {
                viewGroupOptions(scanner, groupId);
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
}
