package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.SQLException;

//Controller class for new driver screen
public class NewDriverController {
	
//	JavaFX screen elements
    @FXML private TextField driverLicenseNumberTextField;
    @FXML private Label driverLicenseNumberErrorLabel;
    @FXML private TextField firstNameTextField;
    @FXML private Label firstNameErrorLabel;
    @FXML private TextField lastNameTextField;
    @FXML private Label lastNameErrorLabel;
    @FXML private DatePicker dOBDatePicker;
    @FXML private TextArea notesTextArea;
    @FXML private Button submitButton;

    private Driver driver = new Driver();

    private String defaultStyle;

//  Method to initialize the controller
    public void initialize() {
        defaultStyle = dOBDatePicker.getStyle();
        submitButton.setOnAction(event -> {
            try {
                handleSubmitButton();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
//  Methods to implement screen elements actions
    @FXML
    private void handleSubmitButton() throws SQLException {
        // Check if fields are correct
        if (!validSubmission()) {
            System.out.println("Inputs invalid");
            return;
        }

        // Create a new Driver object with the values entered in the form fields
        driver.setDLNumber(driverLicenseNumberTextField.getText());
        driver.setFirstName(firstNameTextField.getText());
        driver.setLastName(lastNameTextField.getText());
        driver.setDateOfBirth(dOBDatePicker.getValue());
        driver.setLicenseStatus("Active");
        driver.setWarrantStatus(false);
        driver.setNotes(notesTextArea.getText());

        try {
            driver.save(); // Save the driver to the database
            System.out.println("Driver saved successfully!");
            // close current window
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Driver save unsuccessful");
        }
    }

    private boolean validSubmission() {
        boolean isValid = true;

        // Driver's License Validation
        if (driverLicenseNumberTextField.getText() == null || driverLicenseNumberTextField.getText().isEmpty()
                || !driverLicenseNumberTextField.getText().matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) {
            driverLicenseNumberErrorLabel.setVisible(true);
            isValid = false;
        } else {
            driverLicenseNumberErrorLabel.setVisible(false);
        }

        // First Name Validation
        if (firstNameTextField.getText() == null || firstNameTextField.getText().isEmpty()) {
            firstNameErrorLabel.setVisible(true);
            isValid = false;
        } else {
            firstNameErrorLabel.setVisible(false);
        }

        // Last Name Validation
        if (lastNameTextField.getText() == null || lastNameTextField.getText().isEmpty()) {
            lastNameErrorLabel.setVisible(true);
            isValid = false;
        } else {
            lastNameErrorLabel.setVisible(false);
        }

        // Date of Birth Validation
        if (dOBDatePicker.getValue() == null) {
            dOBDatePicker.setStyle("-fx-background-color: red");
            isValid = false;
        } else {
            dOBDatePicker.setStyle(defaultStyle);
        }
        return isValid;
    }
}
