package com.budgetbuddy.dao;

import com.budgetbuddy.models.User;
import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.util.Queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements IAdminDAO{
    private static AdminDAOImpl adminDAOImpl = null;

    private AdminDAOImpl(){

    }

    public static IAdminDAO getInstance(){
        if(adminDAOImpl == null){
            adminDAOImpl = new AdminDAOImpl();
        }

        return adminDAOImpl;
    }
    public List<User> getTotalUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try(Statement statement = DatabaseConfiguration.getConnection().createStatement()){
            ResultSet resultSet = statement.executeQuery(Queries.getTotalUsers());
            while(resultSet.next()){

                users.add(mapUser(resultSet));
            }
        }
        return users;
    }

    private User mapUser(ResultSet resultSet) throws SQLException{
        User user = new User();
        user.setUsername(resultSet.getString("username"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        return user;

    }

    public List<User> getActiveUsers() throws SQLException {
        List<User> activeUsers = new ArrayList<>();
        try(Statement statement = DatabaseConfiguration.getConnection().createStatement()){
            ResultSet resultSet = statement.executeQuery(Queries.getActiveUsers());
            while (resultSet.next()){
                activeUsers.add(mapUser(resultSet));
            }
        }
        return activeUsers;
    }

    public List<User> getRemovedUsers() throws SQLException {
        List<User> removedUsers = new ArrayList<>();
        try(Statement statement = DatabaseConfiguration.getConnection().createStatement()){
            ResultSet resultSet = statement.executeQuery(Queries.getRemovedUsers());
            while (resultSet.next()){
                removedUsers.add(mapUser(resultSet));
            }
        }
        return removedUsers;
    }
}
