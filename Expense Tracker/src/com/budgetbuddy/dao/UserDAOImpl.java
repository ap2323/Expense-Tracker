package com.budgetbuddy.dao;

import com.budgetbuddy.models.Group;
import com.budgetbuddy.models.User;
import com.budgetbuddy.util.DatabaseConfiguration;
import com.budgetbuddy.util.Queries;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class UserDAOImpl implements IUserDAO {
    private static UserDAOImpl userDAOImpl = null;

    private UserDAOImpl(){

    }

    public static IUserDAO getInstance() {
        if(userDAOImpl == null){
            userDAOImpl = new UserDAOImpl();
        }
        return userDAOImpl;
    }

    public User login(String email, String password) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.getLogin_Query())) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapUsers(resultSet);
            }
        }
        return null;
    }

    public boolean register(User user) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.getRegisterQuery())) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getStatus());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public  boolean isAlreadyRegistered(String mail) throws SQLException{

        try (Connection connection = DatabaseConfiguration.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.getIsAlreadyRegisterQuery())) {
            preparedStatement.setString(1, mail);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }
        return false;
    }

    public boolean isValidUsername(String username) throws SQLException {
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getIsValidUsernameQuery())){

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1) > 0;
            }

        }
        return false;
    }

    public void update(int userId, User newUser) throws SQLException{
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getUpdateQuery())){

            statement.setString(1, newUser.getUsername());
            statement.setString(2, newUser.getEmail());
            statement.setString(3, newUser.getPassword());
            statement.setTimestamp(4, newUser.getUpdatedAt());

            statement.setInt(5, userId);

            statement.executeUpdate();
        }
    }

    public void updateStatus(int user_id, String status) throws SQLException{

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getUpdateStatusQuery())){
            statement.setString(1, status);
            statement.setInt(2, user_id);

            statement.executeUpdate();
        }

    }

    public int getUserId(String mail) throws SQLException{

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getUserIdQuery())){
            statement.setString(1, mail);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("user_id");
            }
        }
        return 0;
    }

    public void registerReferral(int userId, int referralId) throws SQLException {

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getRegisterReferralQuery())) {
            statement.setInt(1, userId);
            statement.setInt(2, referralId);

            statement.executeUpdate();
        }
    }

    public List<User> getReferralUsers(int userId) throws SQLException {

        List<User> users = new ArrayList<>();

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getReferralUsersQuery())){
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                users.add(mapUsers(resultSet));
            }
        }
        return users;
    }

    private User mapUsers(ResultSet resultSet) throws SQLException{
        User user = new User(resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"));

        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        user.setStatus(resultSet.getString("status"));

        return user;
    }

    public void deleteAccount(int userId) throws SQLException{

        updateStatus(userId, "removed");
    }

    public boolean isAlreadyReferred(int userId, int referralId) throws SQLException{

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(Queries.getIsAlreadyReferredQuery())){
            statement.setInt(1, userId);
            statement.setInt(2, referralId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    public void createGroup(int userId, String groupName) throws  SQLException{
        String query = "INSERT INTO expense_groups(group_name) VALUES (?)";
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, groupName);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int group_id = 0;
            if (generatedKeys.next()) {
                group_id = generatedKeys.getInt(1);
            }

            addMembers(userId, group_id);
        }
    }

    public boolean isAlreadyMember(int userId, int groupId) throws SQLException{
        String query = "SELECT COUNT(user_id) FROM group_members WHERE user_id = ? AND group_id = ?";
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(query)){
            statement.setInt(1, userId);
            statement.setInt(2, groupId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    public void addMembers(int userId, int groupId) throws SQLException {

        String query = "INSERT INTO group_members(group_id,user_id, expense_id) VALUES( ?, ?, ?)";

        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(query)){
            statement.setInt(1, groupId);
            statement.setInt(2, userId);
            statement.setInt(3, 0);

            statement.executeUpdate();
        }
    }

    public List<Group> getGroups(int userId) throws SQLException{
        List<Group> groups = new ArrayList<>();
        String query = "SELECT DISTINCT e.group_id, e.group_name FROM expense_groups e INNER JOIN group_members g ON g.group_id = e.group_id WHERE user_id = ?";
        try(PreparedStatement statement = DatabaseConfiguration.getConnection().prepareStatement(query)){
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                groups.add(new Group(resultSet.getInt("group_id"),
                        resultSet.getString("group_name")));
            }
        }
        return groups;
    }

    public String getUsername(int userId) throws SQLException {
        String query = "SELECT username FROM users WHERE user_id = ?";

        try(PreparedStatement preparedStatement = DatabaseConfiguration.getConnection().prepareStatement(query)){

            preparedStatement.setInt(1, userId);

           ResultSet resultSet = preparedStatement.executeQuery();

           if(resultSet.next()){
               return resultSet.getString("username");
           }
        }
        return null;
    }

    public List<String> getGroupUsername(int groupId) throws SQLException{
        List<String> users = new ArrayList<>();

        String query = "SELECT DISTINCT u.username FROM group_members g INNER JOIN users u ON g.user_id = u.user_id WHERE g.group_id = ?";

        try(PreparedStatement preparedStatement = DatabaseConfiguration.getConnection().prepareStatement(query)){
            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                users.add(resultSet.getString("username"));
            }
        }
        return users;
    }
    public void addGroupExpenses(int userId, int groupId, int expenseId) throws SQLException{
        if(checkUserExpenseId(userId, groupId)){
            String query = "UPDATE group_members set expense_id = ? WHERE group_id = ? AND user_id = ?";

            try(PreparedStatement preparedStatement = DatabaseConfiguration.getConnection().prepareStatement(query)){
                preparedStatement.setInt(1, expenseId);
                preparedStatement.setInt(2, groupId);
                preparedStatement.setInt(3, userId);

                preparedStatement.executeUpdate();
            }
        } else {
            String query = "INSERT INTO group_members(group_id, user_id, expense_id) VALUES ( ?, ?, ?)";
            try(PreparedStatement preparedStatement = DatabaseConfiguration.getConnection().prepareStatement(query)){
                preparedStatement.setInt(1, groupId);
                preparedStatement.setInt(2, userId);
                preparedStatement.setInt(3, expenseId);

                preparedStatement.executeUpdate();
            }
        }

    }

    private boolean checkUserExpenseId(int userId, int groupId) throws SQLException{

        String query = "SELECT expense_id FROM group_members WHERE user_id = ? AND group_id = ?";

        try(PreparedStatement preparedStatement = DatabaseConfiguration.getConnection().prepareStatement(query)){
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, groupId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("expense_id") == 0;
            }
        }
        return false;
    }
}

