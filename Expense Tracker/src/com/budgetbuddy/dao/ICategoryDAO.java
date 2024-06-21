package com.budgetbuddy.dao;

import com.budgetbuddy.models.Category;

import java.sql.SQLException;
import java.util.List;

public interface ICategoryDAO {
    List<Category> getAllCategories() throws SQLException;
    Category getCategory(int categoryId) throws SQLException;

}
