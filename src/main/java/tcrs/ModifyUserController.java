package tcrs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Label;

//Controller class for modify user screen
public class ModifyUserController {
	
//	JavaFX screen elements
    @FXML ComboBox<String> userType;
    @FXML ComboBox<String> userStatus;
    @FXML Label userUsername;
    @FXML TextField userPassword;
    @FXML TextField userName;
    @FXML Label userTypeError;
    @FXML Label userStatusError;
    @FXML Label userPasswordError;
    @FXML Label userNameError;
    
    private User currUser;
    
//  Method to initialize the controller
    public void initialize(String currUsername) throws Exception {
        currUser = User.searchUser(currUsername);
        System.out.println(currUser.toString());
        userUsername.setText("Username: " + currUsername);
        userType.getItems().addAll("Admin", "Local");
        userStatus.getItems().addAll("Active", "Inactive");
    }
    
//  Methods to implement screen elements actions
    @FXML
    public void deleteCurrentUser() throws SQLException {
    	 Alert alert = new Alert(AlertType.CONFIRMATION);
         alert.setTitle("Delete User");
         alert.setHeaderText(null);
         alert.setContentText("Are you sure you want to delete this user?");

         Optional<ButtonType> result = alert.showAndWait();
         if (result.get() == ButtonType.OK) {
             // User confirmed deletion, so delete the citation
             currUser.deleteUser();
             // get a reference to the stage
             Stage stage = (Stage) userUsername.getScene().getWindow();
             // close the stage
             stage.close();
         } else {
             // User cancelled deletion
         }
    }

    @FXML
    public void saveUserInfo() throws SQLException {
        // Create a new Citation object with the values entered in the form fields
        currUser.setType(userType.getValue());
        currUser.setStatus(userStatus.getValue());
        currUser.setPassword(userPassword.getText());
        currUser.setName(userName.getText());
        
        if (userType.getValue() == null) {
        	userTypeError.setText("Type not selected");
        	userTypeError.setVisible(true);
        	return;
        }
        userTypeError.setText("");
        userTypeError.setVisible(false);
        
        if (userStatus.getValue() == null) {
        	userStatusError.setText("Status not selected");
        	userStatusError.setVisible(true);
        	return;
        }
        userStatusError.setText("");
        userStatusError.setVisible(false);
        
        if (userPassword.getText().length() <= 6) {
        	userPasswordError.setText("Password less than 7 characters");
        	userPasswordError.setVisible(true);
        	return;
        }
        userPasswordError.setText("");
        userPasswordError.setVisible(false);
        
        if (userName.getText().length() <= 2) {
        	userNameError.setText("Name less than 3 characters");
        	userNameError.setVisible(true);
        	return;
        }
        userNameError.setText("");
        userNameError.setVisible(false);
        
        
    	try {
    		currUser.updateUser(); // Update the citation in the database
            System.out.println("User updated successfully!");
            Stage stage = (Stage) userUsername.getScene().getWindow();
            // close the stage
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	
    }
}
