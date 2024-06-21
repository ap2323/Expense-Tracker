package com.budgetbuddy.models;

import java.sql.Date;
import java.sql.Time;

public class Expense {
    private int expenseId;
    private int userId;
    private Category category;
    private float amount;
    private String description;
    private Date expenseDate;

    private Time time;

    public Expense(int userId, Category category, float amount) {
       this.userId = userId;
       this.category = category;
       this.amount = amount;
    }

    public Expense(int expenseId,int userId, Category category, float amount) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public Date getDate() {
        return  expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
       return category.getCategoryName() + "\t\t" + amount +"\t\t" + expenseDate + "\t\t" + description;
    }
}

