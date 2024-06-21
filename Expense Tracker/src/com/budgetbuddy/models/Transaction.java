package com.budgetbuddy.models;

import java.sql.Date;
import java.sql.Time;

public class Transaction {
    private int id;
    private int userId;
    private Category category;
    private float amount;
    private String description;
    private Date transactionDate;

    private Time time;

    public Transaction(int id, int userId, Category category, float amount) {
        this(userId, category, amount);
        this.id = id;
    }

    public Transaction(int userId, Category category, float amount) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Date getDate() {
        return transactionDate;
    }

    public void setDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return  category.getCategoryType() + "\t\t\t" + category.getCategoryName() + "\n" +
                amount + "\t\t\t" + transactionDate;
    }

}

