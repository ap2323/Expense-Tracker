package com.budgetbuddy.models;

public class Category {
    private int categoryId;
    private String categoryName;
    private CategoryType categoryType;

    public Category(int categoryId, String categoryName, CategoryType categoryType) {
        this(categoryId, categoryName);
        this.categoryType = categoryType;
    }
    public Category(int categoryId, String categoryName){
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public String toString() {
       return categoryId + "\t" + categoryName;
    }
}
