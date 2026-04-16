package tcrs;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

//Controller class for new user screen
public class NewUserController {
	
//	JavaFX screen elements
	@FXML TextField usernameTextField;
    @FXML ComboBox<String> setAccountType;
    @FXML ComboBox<String> setAccountStatus;
    @FXML TextField passwordTextField;
    @FXML TextField nameTextField;
    @FXML Button submitUserButton;
    @FXML Label usernameErrorLabel;
    @FXML Label typeErrorLabel;
    @FXML Label statusErrorLabel;
    @FXML Label passwordErrorLabel;
    @FXML Label nameErrorLabel;
    
    private User user = new User();

//  Method to initialize the controller
    public void initialize() {

    	setAccountType.getItems().addAll("Admin", "Local");
    	setAccountStatus.getItems().addAll("Active", "Inactive");

    }

//  Methods to implement screen elements actions
    @FXML
    public void createUser() throws IOException, SQLException {

        // Check if foreign keys exists
        if (User.usernameExists(usernameTextField.getText()) || usernameTextField.getText().length() <= 2) {
        	usernameErrorLabel.setText("Username does not exist, or less than 3 characters");
        	usernameErrorLabel.setVisible(true);
            return;
        }
        usernameErrorLabel.setText("");
        usernameErrorLabel.setVisible(false);

        if (setAccountType.getValue() == null) {
        	typeErrorLabel.setText("Type not selected");
        	typeErrorLabel.setVisible(true);
        	return;
        }
        typeErrorLabel.setText("");
        typeErrorLabel.setVisible(false);
        
        if (setAccountStatus.getValue() == null) {
        	statusErrorLabel.setText("Status not selected");
        	statusErrorLabel.setVisible(true);
        	return;
        }
        statusErrorLabel.setText("");
        statusErrorLabel.setVisible(false);
        
        if (passwordTextField.getText().length() <= 6) {
        	passwordErrorLabel.setText("Password less than 7 characters");
        	passwordErrorLabel.setVisible(true);
        	return;
        }
        passwordErrorLabel.setText("");
        passwordErrorLabel.setVisible(false);
        
        if (nameTextField.getText().length() <= 2) {
        	nameErrorLabel.setText("Name less than 3 characters");
        	nameErrorLabel.setVisible(true);
        	return;
        }
        nameErrorLabel.setText("");
        nameErrorLabel.setVisible(false);
        
        // Create a new Citation object with the values entered in the form fields
        user.setUsername(usernameTextField.getText());
        user.setType(setAccountType.getValue());
        user.setStatus(setAccountStatus.getValue());
        user.setPassword(passwordTextField.getText());
        user.setName(nameTextField.getText());
        
        try {
            user.saveUser(); // Save the citation to the database
            // close current window
            Stage stage = (Stage) submitUserButton.getScene().getWindow();
            stage.close();
            System.out.println("User save successful");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("User save unsuccessful");
        }
    }
}
