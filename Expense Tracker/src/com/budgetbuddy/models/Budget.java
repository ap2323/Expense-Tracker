package com.budgetbuddy.models;
import java.util.Date;

public class Budget {
    private int budgetId;
    private int userId;
    private float amount;
    private Date date;

    public Budget(int budgetId, int userId, float amount) {
        this(userId, amount);
        this.budgetId = budgetId;
    }

    public Budget(int userId, float amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters and setters
    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

