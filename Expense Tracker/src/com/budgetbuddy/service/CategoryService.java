package com.budgetbuddy.service;

import com.budgetbuddy.dao.CategoryDAOImpl;
import com.budgetbuddy.dao.ICategoryDAO;
import com.budgetbuddy.models.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final ICategoryDAO categoryDAO;

    public CategoryService(){
        this.categoryDAO = CategoryDAOImpl.getInstance();
    }

    public List<Category> getCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }

    public Category getCategory(int categoryId) throws SQLException {
       return categoryDAO.getCategory(categoryId);
    }

}
