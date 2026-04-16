package tcrs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

//Class for user creation
public class User {
	
//User elements
    private String username;
    private String password;
    private String type;
    private String status;
    private String name;
    
    public boolean isValid; 

     // SQL Server and Credentials
     private static String url = "jdbc:mysql://localhost:3306/project";
     private static String usernameServer = "root";
     private static String passwordServer = "test";

//     Constructors
     public User() {
         
     }

    public User(String username, String password, String type, String name, String status) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.status = status;
        this.name =name;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters for all fields

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    Method to search a user
    public static User searchUser(String searchUsername) throws Exception {
        String query = "SELECT * FROM Users WHERE Username = ?";
        User user = null;

        try (Connection conn = DriverManager.getConnection(url, usernameServer, passwordServer)) {
            PreparedStatement preparedStatement = null;
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, searchUsername);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                String type = resultSet.getString("Type");
                String status = resultSet.getString("Status");
                String name = resultSet.getString("Name");

                user = new User(username, password, type, name, status);
            }
        } catch (SQLException e) {
            System.out.println("Error searching user: " + e.getMessage());
        }

        return user;
    }
    
    //Save information in database
    public void saveUser() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, usernameServer, passwordServer)) {
            String insertUserSql = "INSERT INTO Users (Username, Password, Type, Name, Status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserSql);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.password);
            preparedStatement.setString(3, this.type);
            preparedStatement.setString(4, this.name);
            preparedStatement.setString(5, this.status);

            preparedStatement.executeUpdate();
        }
    }

    // Update User
    public void updateUser() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, usernameServer, passwordServer)) {
            String insertUserSql = "UPDATE Users SET Password = ?, Type = ?, Name = ?, Status = ? WHERE Username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserSql);
            preparedStatement.setString(1, this.password);
            preparedStatement.setString(2, this.type);
            preparedStatement.setString(3, this.name);
            preparedStatement.setString(4, this.status);
            preparedStatement.setString(5, this.username);

            preparedStatement.executeUpdate();
        }
    }

    //List all users in database
    public static List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();

//        try (Connection connection = DatabaseUtils.getConnection()) {
        try (Connection connection = DriverManager.getConnection(url, usernameServer, passwordServer)) {
            String selectUserSql = "SELECT * FROM Users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectUserSql);

            while (resultSet.next()) {
                String userName = resultSet.getString("Username");
                String user_password = resultSet.getString("Password");
                String type = resultSet.getString("Type");
                String activeStatus = resultSet.getString("Status");
                String name = resultSet.getString("Name");

                User user = new User (userName, user_password, type, name, activeStatus);
                users.add(user);
            }
        }
        return users;
    }

    //Delete this User
    public void deleteUser() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, usernameServer, passwordServer)) {
            String deleteUserSql = "DELETE FROM Users WHERE Username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSql);
            preparedStatement.setString(1, this.username);

            preparedStatement.executeUpdate();
        }
    }
  
//    Checking if username exists
    public static boolean usernameExists(String userUsername) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, usernameServer, passwordServer)) {
            String selectSql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, userUsername);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }
}
