package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;

import java.sql.SQLException;
import java.util.Optional;

//Controller class for modify vehicle screen
public class ModifyVehicleController {
	
//	JavaFX screen elements	
    @FXML private TextField vinTextField;
    @FXML private Label vinErrorLabel;
    @FXML private TextField makeTextField;
    @FXML private Label makeErrorLabel;
    @FXML private TextField modelTextField;
    @FXML private Label modelErrorLabel;
    @FXML private TextField yearTextField;
    @FXML private Label yearErrorLabel;
    @FXML private RadioButton registeredRadioButton;
    @FXML private RadioButton unregisteredRadioButton;
    @FXML private RadioButton stolenRadioButton;
    @FXML private RadioButton notStolenRadioButton;
    @FXML private RadioButton warrantsYesRadioButton;
    @FXML private RadioButton warrantsNoRadioButton;
    @FXML private Button modifyButton;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;
    @FXML private Button deleteButton;
    
    private String defaultEntryStyle;
    private String uneditableEntryStyle;

    private Vehicle vehicle;
    private String currVehicleID;

//  Method to initialize the controller
    public void initialize(String currVIN) {
        // assign defaults
        this.currVehicleID = currVIN;
        defaultEntryStyle = makeTextField.getStyle();
        uneditableEntryStyle = "-fx-background-color: transparent";

        try {
            vehicle = Vehicle.searchVehicle(currVIN);
            // Populate page with current vehicle values
            vinTextField.setText(vehicle.getVIN());
            makeTextField.setText(vehicle.getMake());
            modelTextField.setText(vehicle.getModel());

            boolean registrationStatus = vehicle.isRegistrationStatus();
            if (registrationStatus) {
                registeredRadioButton.setSelected(true);
            } else {
                unregisteredRadioButton.setSelected(true);
            }

            boolean stolenStatus = vehicle.isStolenStatus();
            if (stolenStatus) {
                stolenRadioButton.setSelected(true);
            } else {
                notStolenRadioButton.setSelected(true);
            }

            boolean warrantStatus = vehicle.isWarrantStatus();
            if (warrantStatus) {
                warrantsYesRadioButton.setSelected(true);
            } else {
                warrantsNoRadioButton.setSelected(true);
            }

            setEditableAll(false);

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

    @FXML
    private void handleCancelButton() {
        initialize(currVehicleID);
    }

    @FXML
    private void handleDeleteButton() throws SQLException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Vehicle");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this vehicle?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // User confirmed deletion, so delete the vehicle
            try {
                vehicle.deleteVehicle();
                // get a reference to the stage
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                // close the stage
                stage.close();
            } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Cannot delete");
                if (e.toString().contains("citations_ibfk_2")) {
                    Alert alert1 = new Alert(AlertType.ERROR);
                    alert1.setTitle("Delete error");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Vehicle has outstanding citations");
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
    
        // Create a new Vehicle object with the values entered in the form fields
        vehicle.setVIN(vinTextField.getText());
        vehicle.setMake(makeTextField.getText());
        vehicle.setModel(modelTextField.getText());
    
        // Update registration, stolen, and warrant status
        vehicle.setRegistrationStatus(registeredRadioButton.isSelected());
        vehicle.setStolenStatus(stolenRadioButton.isSelected());
        vehicle.setWarrantStatus(warrantsYesRadioButton.isSelected());
    
        try {
            vehicle.updateVehicle(); // Update the vehicle in the database
            System.out.println("Vehicle updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setEditableAll(false);
    }
    

    private boolean validSubmission() {
        boolean isValid = true;
        return isValid;
    }

    // Set all entries uneditable
    private void setEditableAll(boolean editable) {
        vinTextField.setStyle(false ? defaultEntryStyle : uneditableEntryStyle);
        makeTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        makeTextField.setEditable(editable);
        modelTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        modelTextField.setEditable(editable);
        yearTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        yearTextField.setEditable(editable);
    
        // Set registration, stolen, and warrant status radio buttons editable
        registeredRadioButton.setDisable(!editable);
        unregisteredRadioButton.setDisable(!editable);
        stolenRadioButton.setDisable(!editable);
        notStolenRadioButton.setDisable(!editable);
        warrantsYesRadioButton.setDisable(!editable);
        warrantsNoRadioButton.setDisable(!editable);
    
        applyButton.setVisible(editable);
        modifyButton.setVisible(!editable);
        cancelButton.setVisible(editable);
        deleteButton.setVisible(editable);
    }
    
}
