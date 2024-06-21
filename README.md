# Expense Tracker

## Table of Contents

* [About Expense Tracker](#about-expense-tracker)
* [Features](#features)
* [Technologies Used](#technologies-used)
* [Setup](#setup)
* [Database Configuration](#database-configuration)
* [Usage](#usage)
* [Contributing](#contributing)

## About Expense Tracker
Expense Tracker is a comprehensive application designed to help users manage their finances by tracking income and expenses. 
The application provides insightful reports and analysis to help users understand their spending habits and make informed financial decisions.

## Features
- **User Authentication:** Secure login and registration system.
- **Add Transactions:** Easily add income and expense entries.
- **Categorize Transactions:** Assign categories to transactions for better organization.
- **Generate Reports:** View detailed reports and summaries of financial data.
- **User-friendly Interface:** Intuitive and easy-to-use interface.
- **Groups:** User can create groups for trip planning, etc.,
- **Multi-user Accounts:** User can create multiple accounts and change accounts.

## Technologies Used
* **Java:** Core programming language used for application development.
* **JDBC:** Java Database Connectivity for database interaction.
* **MySQL:** Database management system.

## Setup
## Prerequisites
* Java Development Kit (JDK) 8 or higher
* MySQL

# Installation
## Clone the repository:
```bash
git clone https://github.com//expense-tracker.git
cd expense-tracker
```
## Database Configuration
Update the dbconfig.properties file with your MySQL credentials.
```properties
database.name= expense_tracker
database.user=yourusername
database.password=yourpassword
```
# Database Configuration
The DatabaseConfiguration class sets up the database and creates the necessary tables. Ensure you have provided correct MySQL credentials in the dbconfig.properties file.
## Tables
* Users
* referral accounts
* Budget
* expenses
* income
* transactions
* expense groups
* group members

## Usage
* User Registration.
* Run the application.
* Follow the prompts to register a new user.
  
  ![Screenshot from 2024-06-21 16-38-13](https://github.com/ap2323/Expense-Tracker/assets/91046006/31069814-14b4-4c0c-8544-5cd7ebeb7846)

* Log in using your credentials.

  ![Screenshot from 2024-06-21 16-38-44](https://github.com/ap2323/Expense-Tracker/assets/91046006/e815766d-62dd-45f9-9b56-1eaca500ede1)

  Home page loads current month expenses and income.

* Add Tranasction

  ![Screenshot from 2024-06-21 16-47-58](https://github.com/ap2323/Expense-Tracker/assets/91046006/a408aa41-302c-4a21-9c3e-a877058d0921)

* After add expense.
  
  ![Screenshot from 2024-06-21 16-49-50](https://github.com/ap2323/Expense-Tracker/assets/91046006/e27adb65-c0d8-46fb-b297-cada2194a471)

* Set the budget.

  ![Screenshot from 2024-06-21 16-50-12](https://github.com/ap2323/Expense-Tracker/assets/91046006/da4957df-15cf-4c83-b468-d030f273e44c)

* After add multiple expenses and income.

  ![Screenshot from 2024-06-21 16-52-37](https://github.com/ap2323/Expense-Tracker/assets/91046006/3829e6ae-ebc1-4ec0-b6d1-d8886d1853f7)

* View Income.
  
  ![Screenshot from 2024-06-21 16-52-51](https://github.com/ap2323/Expense-Tracker/assets/91046006/d88fe2bb-ed49-401a-9427-a31fbe4a18df)

* View Expenses.

  ![Screenshot from 2024-06-21 16-52-58](https://github.com/ap2323/Expense-Tracker/assets/91046006/fc24883f-9978-4b95-a815-9de44e7f5d3f)

* Edit transaction
  
  ![Screenshot from 2024-06-21 16-56-38](https://github.com/ap2323/Expense-Tracker/assets/91046006/fe11f982-4f34-4a41-9716-9e8e7b7eb0da)


  ![Screenshot from 2024-06-21 16-56-45](https://github.com/ap2323/Expense-Tracker/assets/91046006/5ffd07f3-855f-4fe0-a09e-c10b4c8b74d1)

* Remove transaction

  ![Screenshot from 2024-06-21 16-58-23](https://github.com/ap2323/Expense-Tracker/assets/91046006/324a7a29-de89-4729-9187-5787794046af)

* Create Group

  ![Screenshot from 2024-06-21 17-00-07](https://github.com/ap2323/Expense-Tracker/assets/91046006/b80cfa72-946f-460c-a23b-c6ab4cace7d3)

* View Group

  ![Screenshot from 2024-06-21 17-02-27](https://github.com/ap2323/Expense-Tracker/assets/91046006/b5c8b53c-766c-49de-aaf1-e8839565ca20)

  After adding another user for add to group.

* Add member and view members.

  ![Screenshot from 2024-06-21 17-02-46](https://github.com/ap2323/Expense-Tracker/assets/91046006/71544bd4-b3f4-444a-9de6-78845fe5599a)

* Add expense to group and view Expense

  ![Screenshot from 2024-06-21 17-06-00](https://github.com/ap2323/Expense-Tracker/assets/91046006/352e075f-9527-40aa-8abb-2d4213b9a61a)

  ![Screenshot from 2024-06-21 17-06-07](https://github.com/ap2323/Expense-Tracker/assets/91046006/98aa9c40-15dd-4adc-8a84-a25269add304)

  
* Multi-Account

  In Settings, you can see the multi-account option and other options.

  ![Screenshot from 2024-06-21 17-07-30](https://github.com/ap2323/Expense-Tracker/assets/91046006/a180355f-99e3-4d63-ace3-9dfecdc9f8be)

  Add Account and Change Account

  ![Screenshot from 2024-06-21 17-09-48](https://github.com/ap2323/Expense-Tracker/assets/91046006/a685744c-d64b-4c87-aa04-a3db638419ab)

  After changing,

  ![Screenshot from 2024-06-21 17-09-53](https://github.com/ap2323/Expense-Tracker/assets/91046006/ed42d2bf-8061-48f3-9519-15fa09cd5c22)




# Contributing
We welcome contributions from the community! If you find a bug or want to add a feature, please open an issue or submit a pull request.

## Steps to Contribute
* Fork the repository.
* Create a new branch.
* Make your changes.
* Submit a pull request.

      
# TRUST YOUR CRAZY IDEAS!.   
      
      
      
