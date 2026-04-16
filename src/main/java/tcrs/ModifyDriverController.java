package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.*;
import java.util.Optional;

//Controller class for modify driver screen
public class ModifyDriverController {
	
//	JavaFX screen elements
    @FXML private Label driverIdLabel;
    @FXML private TextField driverLicenseNumberTextField;
    @FXML private Label driverLicenseNumberErrorLabel;
    @FXML private TextField firstNameTextField;
    @FXML private Label firstNameErrorLabel;
    @FXML private TextField lastNameTextField;
    @FXML private Label lastNameErrorLabel;
    @FXML private DatePicker dOBDatePicker;
    @FXML private TextArea drivingRecordTextArea;
    @FXML private RadioButton licenseActiveRadioButton;
    @FXML private RadioButton licenseNotActiveRadioButton;
    @FXML private RadioButton licenseSuspendedRadioButton;
    @FXML private RadioButton warrantsYesRadioButton;
    @FXML private RadioButton warrantsNoRadioButton;
    @FXML private TextArea notesTextArea;
    @FXML private Button cancelButton;
    @FXML private Button deleteButton;
    @FXML private Button modifyButton;
    @FXML private Button applyButton;

    private String defaultEntryStyle;
    private String uneditableEntryStyle;
    private String defaultStyleDP;

    private Driver driver;
    private String currDLNumber;

//  Method to initialize the controller
    public void initialize(String currDLNumber) {
        // assign defaults
        this.currDLNumber = currDLNumber;
        defaultEntryStyle = firstNameTextField.getStyle();
        defaultStyleDP = dOBDatePicker.getStyle();
        uneditableEntryStyle = "-fx-background-color: transparent";

        // Start entry fields uneditable and remove background
        setEditableAll(false);
        driverLicenseNumberTextField.setEditable(false);
        driverLicenseNumberTextField.setStyle(uneditableEntryStyle);
        firstNameTextField.requestFocus();

        try {
            driver = Driver.searchDriver(currDLNumber);
            // Populate page with current driver values
            driverLicenseNumberTextField.setText(driver.getDLNumber());
            firstNameTextField.setText(driver.getFirstName());
            lastNameTextField.setText(driver.getLastName());
            dOBDatePicker.setValue(driver.getDateOfBirth());
            drivingRecordTextArea.setText(driver.getDrivingRecord());
            notesTextArea.setText(driver.getNotes());

            String licenseStatus = driver.getLicenseStatus();
            switch (licenseStatus) {
                case "Active":
                    licenseActiveRadioButton.setSelected(true);
                    break;

                case "Not Active":
                    licenseNotActiveRadioButton.setSelected(true);
                    break;

                case "Suspended":
                    licenseSuspendedRadioButton.setSelected(true);
                    break;
            }

            boolean warrantStatus = driver.isWarrantStatus();
            if (warrantStatus) {
                warrantsYesRadioButton.setSelected(true);
            } else {
                warrantsNoRadioButton.setSelected(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        modifyButton.setOnAction(event -> handleModifyButton());
        applyButton.setOnAction(event -> {
            try {
                handleApplyButton();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
//  Methods to implement screen elements actions
    @FXML
    private void handleCancelButton() {
        initialize(currDLNumber);
    }

    @FXML
    private void handleDeleteButton() throws SQLException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Driver");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this driver?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // User confirmed deletion, so delete the driver
            try {
                driver.deleteDriver();
                // get a reference to the stage
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                // close the stage
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Cannot delete");
                if (e.toString().contains("citations_ibfk_1")) {
                    Alert alert1 = new Alert(AlertType.ERROR);
                    alert1.setTitle("Delete error");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Driver has outstanding citations");
                    alert1.showAndWait();
                }
            }
        } else {
            // User cancelled deletion
        }
    }

    @FXML
    private void handleModifyButton() {
        setEditableAll(true);
    }

    @FXML
    private void handleApplyButton() throws SQLException {

        // Check if fields are correct
        if (!validSubmission()) {
            System.out.println("Input invalid");
            return;
        }

        // Update driver object with the values entered in the form fields
        driver.setDLNumber(driverLicenseNumberTextField.getText());
        driver.setFirstName(firstNameTextField.getText());
        driver.setLastName(lastNameTextField.getText());
        driver.setDateOfBirth(dOBDatePicker.getValue());
        driver.setDrivingRecord(drivingRecordTextArea.getText());
        driver.setNotes(notesTextArea.getText());

        if (licenseActiveRadioButton.isSelected()) {
            driver.setLicenseStatus("Active");
        }
        if (licenseNotActiveRadioButton.isSelected()) {
            driver.setLicenseStatus("Not Active");
        }
        if (licenseSuspendedRadioButton.isSelected()) {
            driver.setLicenseStatus("Suspended");
        }

        driver.setWarrantStatus(warrantsYesRadioButton.isSelected());

        try {
            driver.updateDriver(); // Update the driver in the database
            System.out.println("Driver updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setEditableAll(false);
    }

    private boolean validSubmission() {
        boolean isValid = true;

        dOBDatePicker.setStyle(defaultStyleDP);
        if (dOBDatePicker == null) {
            dOBDatePicker.setStyle("-fx-background-color: red");
            isValid = false;
        }

        return isValid;
    }

    // Set all entries editable to true or false
    private void setEditableAll(boolean editable) {

        firstNameTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        firstNameTextField.setEditable(editable);
        lastNameTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        lastNameTextField.setEditable(editable);
        drivingRecordTextArea.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        drivingRecordTextArea.setEditable(editable);
        notesTextArea.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        notesTextArea.setEditable(editable);

        // set radio buttons
        licenseActiveRadioButton.setDisable(!editable);
        licenseNotActiveRadioButton.setDisable(!editable);
        licenseSuspendedRadioButton.setDisable(!editable);
        warrantsYesRadioButton.setDisable(!editable);
        warrantsNoRadioButton.setDisable(!editable);
        dOBDatePicker.setDisable(editable ? false : true);
        applyButton.setVisible(editable);
        modifyButton.setVisible(!editable);
        cancelButton.setVisible(editable);
        deleteButton.setVisible(editable);
    }
}