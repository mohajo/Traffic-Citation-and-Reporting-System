package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.SQLException;

//Controller class for new vehicle screen
public class NewVehicleController {

//	JavaFX screen elements
    @FXML private TextField makeTextField;
    @FXML private Label makeErrorLabel;
    @FXML private TextField modelTextField;
    @FXML private Label modelErrorLabel;
    @FXML private TextField yearTextField;
    @FXML private Label yearErrorLabel;
    @FXML private TextField vinTextField;
    @FXML private Label vinErrorLabel;
    @FXML private TextArea notesTextArea;
    @FXML private Button submitButton;

    private Vehicle vehicle = new Vehicle();

//  Method to initialize the controller
    public void initialize() {
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
        // Check if the vehicle already exists in the database
        if (Vehicle.searchVehicle(vinTextField.getText()) != null) {
            vinErrorLabel.setText("Vehicle already exists");
            vinErrorLabel.setVisible(true);
            return;
        } else {
            vinErrorLabel.setText("*Invalid Input");
            vinErrorLabel.setVisible(false);
        }
        // Create a new Vehicle object with the values entered in the form fields
        vehicle.setMake(makeTextField.getText());
        vehicle.setModel(modelTextField.getText());
        vehicle.setYear(Integer.parseInt(yearTextField.getText()));
        vehicle.setVIN(vinTextField.getText());
        vehicle.setRegistrationStatus(false);
        vehicle.setStolenStatus(false);
        vehicle.setWarrantStatus(false);
        vehicle.setNotes(notesTextArea.getText());

        try {
            vehicle.save(); // Save the vehicle to the database
            System.out.println("Vehicle saved successfully!");
            // close current window
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Vehicle save unsuccessful");
        }
    }

    private boolean validSubmission() {
        boolean isValid = true;

        String regexMakeModel = "^[a-zA-Z0-9\\s]+$";
        String regexYear = "^\\d{4}$";
        String regexVin = "^[0-9A-Z]{17}$";

        // Make Validation
        if (makeTextField.getText() == null || makeTextField.getText().isEmpty()
                || !makeTextField.getText().matches(regexMakeModel)) {
            makeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            makeErrorLabel.setVisible(false);
        }

        // Model Validation
        if (modelTextField.getText() == null || modelTextField.getText().isEmpty()
                || !modelTextField.getText().matches(regexMakeModel)) {
            modelErrorLabel.setVisible(true);
            isValid = false;
        } else {
            modelErrorLabel.setVisible(false);
        }

        // Year Validation
        if (yearTextField.getText() == null || yearTextField.getText().isEmpty()
                || !yearTextField.getText().matches(regexYear)) {
            yearErrorLabel.setVisible(true);
            isValid = false;
        } else {
            yearErrorLabel.setVisible(false);
        }

        // VIN Validation
        if (vinTextField.getText() == null || vinTextField.getText().isEmpty()
                || !vinTextField.getText().matches(regexVin)) {
            vinErrorLabel.setVisible(true);
            isValid = false;
        } else {
            vinErrorLabel.setVisible(false);
        }

        return isValid;
    }
}
