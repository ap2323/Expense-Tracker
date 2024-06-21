package com.budgetbuddy.service;

import com.budgetbuddy.dao.IUserDAO;
import com.budgetbuddy.models.Group;
import com.budgetbuddy.models.User;
import com.budgetbuddy.dao.UserDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final IUserDAO userDAO;

    public UserService() {
        this.userDAO = UserDAOImpl.getInstance();
    }

    public User login(String email, String password) throws SQLException {
        return userDAO.login(email, password);
    }

    public boolean register(User user) throws SQLException {
        return userDAO.register(user);
    }

    public boolean isAlreadyRegistered(String mail) throws SQLException{
        return userDAO.isAlreadyRegistered(mail);
    }

    public boolean isValidUsername(String username) throws SQLException {
        return userDAO.isValidUsername(username);
    }

    public void update(int userId, User newUser) throws SQLException {
        userDAO.update(userId, newUser);
    }

    public int getUserId(String mail) throws SQLException{
        return userDAO.getUserId(mail);
    }

    public void registerReferral(int userId, int referralId) throws SQLException{
        userDAO.registerReferral(userId, referralId);
    }

    public List<User> getReferralUsers(int userId) throws SQLException{
        return userDAO.getReferralUsers(userId);
    }

    public void deleteAccount(int userId) throws SQLException{
        userDAO.deleteAccount(userId);
    }

    public boolean isAlreadyReferred(int userId, int referralId) throws SQLException {
        return userDAO.isAlreadyReferred(userId, referralId);
    }

    public void updateStatus(int userId, String status) throws SQLException {
        userDAO.updateStatus(userId, status);
    }

    public User getUser(String mail, String password) throws SQLException{
        return userDAO.login(mail, password);
    }

    public void createGroup(int userId, String groupName) throws SQLException{
        userDAO.createGroup(userId, groupName);
    }

    public void addMember(int userId, int groupId) throws SQLException{
        userDAO.addMembers(userId, groupId);
    }

    public List<Group> getGroups(int userId) throws SQLException{
        return userDAO.getGroups(userId);
    }

    public void addGroupExpenses(int userId, int groupId, int expenseId)throws SQLException {
        userDAO.addGroupExpenses(userId, groupId, expenseId);
    }

    public String getUsername(int userId) throws SQLException {
        return userDAO.getUsername(userId);
    }

    public List<String> getGroupUsername(int groupId) throws SQLException{
        return userDAO.getGroupUsername(groupId);
    }

    public boolean isAlreadyMember(int userId, int groupId) throws SQLException{
        return userDAO.isAlreadyMember(userId, groupId);
    }
}

