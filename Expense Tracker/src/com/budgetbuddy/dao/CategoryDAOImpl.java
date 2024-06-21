package com.budgetbuddy.dao;

import com.budgetbuddy.models.Category;
import com.budgetbuddy.models.CategoryType;
import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.util.Queries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements ICategoryDAO{

    private static CategoryDAOImpl categoryDAOImpl = null;

    private CategoryDAOImpl(){

    }

    public static ICategoryDAO getInstance(){
        if(categoryDAOImpl == null){
            categoryDAOImpl = new CategoryDAOImpl();
        }

        return categoryDAOImpl;
    }
    public List<Category> getAllCategories() throws SQLException{
        List<Category> categories = new ArrayList<>();

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getAllCategoriesQuery())) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }
        return categories;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("category_id"), rs.getString("category_name"), CategoryType.valueOf(rs.getString("category_type")));
    }

    public Category getCategory(int categoryId) throws SQLException{

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.getCategoryQuery())) {
            stmt.setInt(1,categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCategory(rs);
            }
        }
        return null;
    }

}
