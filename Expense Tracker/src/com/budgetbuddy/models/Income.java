package com.budgetbuddy.models;

import java.sql.Date;
import java.sql.Time;

public class Income {
    private Category category;
    private int incomeId;
    private int userId;
    private float amount;
    private String description;
    private Date date;

    private Time time;

    public Income(int incomeId, Category category,int userId, float amount) {
        this(userId,category ,amount);
        this.incomeId = incomeId;
    }

    public Income(int userId, Category category ,float amount) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
    }

    // Getters and Setters

    public int getId() {
        return incomeId;
    }

    public void setId(int incomeId) {
        this.incomeId = incomeId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return  category.getCategoryName() + "\t\t" + date + "\n" +
                "\t\t" + amount + "\t\t"  + description;
    }
}

