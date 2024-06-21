package com.budgetbuddy.dao;

import com.budgetbuddy.models.User;

import java.sql.SQLException;
import java.util.List;

public interface IAdminDAO {
    List<User> getTotalUsers() throws SQLException;
    List<User> getActiveUsers() throws SQLException;
    List<User> getRemovedUsers() throws SQLException;
}
