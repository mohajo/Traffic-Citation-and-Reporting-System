package tcrs;

import java.sql.*;

import static tcrs.AuthController.user;

// Class to authenticate the user
public class AuthModel {
	
    // SQL Server and Credentials
    private static String url = "jdbc:mysql://localhost:3306/project?useSSL=false";
    private static String username = "root";
    private static String password = "test";
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

//    Authentication method
    public User AuthenticateUser() throws SQLException {

//    	SQL query to select to check username and password in the database
        String myQuery = "select * from Users where username =? and password =?";

//      Executing the query with JD
        try (Connection conn = DriverManager.getConnection(url, username, password)) {;

            preparedStatement = conn.prepareStatement(myQuery);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            resultSet = preparedStatement.executeQuery();

//          Getting user info
            if (resultSet.next()) {
                user.setStatus(resultSet.getString("Status"));
                user.setType(resultSet.getString("Type"));
//               Checking if the user if active or inactive
                if (user.getStatus().toUpperCase().equals("ACTIVE")) {
                    user.isValid = true;
                } else user.isValid = false;
            } else {
                user.isValid = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
