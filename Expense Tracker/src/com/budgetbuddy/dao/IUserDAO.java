package com.budgetbuddy.dao;

import com.budgetbuddy.models.Group;
import com.budgetbuddy.models.User;

import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    User login(String email, String password) throws SQLException;
    boolean register(User user) throws SQLException;
    boolean isAlreadyRegistered(String mail) throws SQLException;
    boolean isValidUsername(String username) throws SQLException;
    void update(int userId, User newUser) throws SQLException;
    void updateStatus(int user_id, String status) throws SQLException;
    int getUserId(String mail) throws SQLException;
    void registerReferral(int userId, int referralId) throws SQLException;
    List<User> getReferralUsers(int userId) throws SQLException;
    void deleteAccount(int userId) throws SQLException;
    boolean isAlreadyReferred(int userId, int referralId) throws SQLException;

    void createGroup(int userId, String groupName) throws SQLException;
    void addMembers(int userId, int groupId) throws SQLException;

    List<Group> getGroups(int userId) throws SQLException;

    void addGroupExpenses(int userId, int groupId, int expenseId) throws SQLException;

    String getUsername(int userId) throws SQLException;

    List<String> getGroupUsername(int groupId) throws SQLException;

    boolean isAlreadyMember(int userId, int groupId) throws SQLException;
}
