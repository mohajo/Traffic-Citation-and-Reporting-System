package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

//Controller class for modify citation screen
public class ModifyCitationController {
   
//	JavaFX screen elements
	@FXML private Label citationIdLabel;
    @FXML private Label dateTimeLabel;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label typeLabel;
    @FXML private TextField customTypeTextField;
    @FXML private TextField driverLicenseNumberTextField;
    @FXML private Label driverLicenseNumberErrorLabel;
    @FXML private TextField licensePlateNumberTextField;
    @FXML private Label licensePlateNumberErrorLabel;
    @FXML private TextField vehicleIdTextField;
    @FXML private Label vehicleIdErrorLabel;
    @FXML private TextField fineAmountTextArea;
    @FXML private Label fineAmountErrorLabel;
    @FXML private TextField officerTextArea;
    @FXML private Label officerErrorLabel;
    @FXML private RadioButton paidRadioButton;
    @FXML private RadioButton unpaidRadioButton;
    @FXML private TextArea notesTextArea;
    @FXML private Label trafficSchoolLabel;
    @FXML private Button bookButton;
    @FXML private Button cancelButton;
    @FXML private Button deleteButton;
    @FXML private Button modifyButton;
    @FXML private Button applyButton;

    private String defaultEntryStyle;
    private String uneditableEntryStyle;

    private Citation citation;
    private int currCitationID;
    private boolean comboBoxAdded = false;

//    Method to initialize the controller
    public void initialize(int currCitationID) {
        // assign defaults
        this.currCitationID = currCitationID;
        defaultEntryStyle = notesTextArea.getStyle();
        uneditableEntryStyle = "-fx-background-color: transparent";

        // Start entry fields uneditable and remove background
        setEditableAll(false);
        
        notesTextArea.requestFocus();
        // get default textfield style to return to
        if (!comboBoxAdded)
            typeComboBox.getItems().addAll("Parking Violation", "Moving Vehicle Violation", "Fix-it Ticket");
        comboBoxAdded = true;
        // hide/show traffic school section
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Moving Vehicle Violation")) {
                trafficSchoolLabel.setVisible(true);
                bookButton.setVisible(true);
            } else {
                trafficSchoolLabel.setVisible(false);
                bookButton.setVisible(false);
            }
        });

        try {
            citation = Citation.searchCitation(currCitationID);
            // Populate page with current citation values get next citation ID, pad left zeroes
            citationIdLabel.setText(String.format("%0" + 8 + "d", citation.getCitationID()));
            dateTimeLabel.setText(citation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            typeLabel.setText(citation.getType());
            typeComboBox.setPromptText(citation.getType());
            typeComboBox.setValue(citation.getType());
            driverLicenseNumberTextField.setText(citation.getDLNumber());
            vehicleIdTextField.setText(citation.getVIN());
            fineAmountTextArea.setText(Double.toString(citation.getFineAmount()));
            officerTextArea.setText(citation.getOfficer());
            notesTextArea.setText(citation.getNotes());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        bookButton.setOnAction(event -> {
            try {
                handleBookButton();
            } catch (NumberFormatException | IOException | SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        modifyButton.setOnAction(event -> handleModifyButton());
        applyButton.setOnAction(event -> {
            try {
                handleApplyButton();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
    
//    Methods to implement screen elements actions
    @FXML
    private void handleBookButton() throws NumberFormatException, IOException, SQLException {
        Load load = new Load();
        load.trafficSchool(Integer.parseInt(citationIdLabel.getText()), citation.getDLNumber());
    }

    @FXML
    private void handleCancelButton() {
        initialize(currCitationID);
    }

    @FXML
    private void handleDeleteButton() throws SQLException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Citation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this citation?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // User confirmed deletion, so delete the citation
            citation.deleteCitation();
            // get a reference to the stage
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            // close the stage
            stage.close();
        } else {
            // User cancelled deletion
        }

    }

    @FXML
    private void handleModifyButton() {
        setEditableAll(true);
    }

    @FXML
    private void handleApplyButton() throws Exception {

        // Check if fields are correct
        if (!validSubmission()) {
            System.out.println("Input invalid");
            return;
        }
        
        // Check if officer / user exists
        if (User.searchUser(officerTextArea.getText()) == null) {
            officerErrorLabel.setText("*Officer does not exist");
            officerErrorLabel.setVisible(true);
            System.out.println("Officer does not exist");
            return;
        }

        // Check if foreign keys exists
        if (!Citation.dLNumberExists(driverLicenseNumberTextField.getText())) {
            driverLicenseNumberErrorLabel.setText("*Driver does not exist");
            driverLicenseNumberErrorLabel.setVisible(true);
            System.out.println("Driver does not exist");
            return;
        }
        driverLicenseNumberErrorLabel.setText("*Invalid Input");
        driverLicenseNumberErrorLabel.setVisible(false);

        if (!Citation.vinExists(vehicleIdTextField.getText())) {
            vehicleIdErrorLabel.setText("*Vehicle does not exist");
            vehicleIdErrorLabel.setVisible(true);
            System.out.println("VIN does not exist");
            return;
        }
        vehicleIdErrorLabel.setText("*Invalid Input");
        vehicleIdErrorLabel.setVisible(false);

        // Create a new Citation object with the values entered in the form fields
        citation.setOfficer(officerTextArea.getText()); // ** This needs to be replaced with user that logged in
        citation.setType(typeComboBox.getValue()); // Replace with actual value from form field
        citation.setDLNumber(driverLicenseNumberTextField.getText());
        citation.setVIN(vehicleIdTextField.getText());
        citation.setFineAmount(Double.parseDouble(fineAmountTextArea.getText())); // Replace with actual value from form
        citation.setPaymentStatus(getPaymentStatus());
        String trafficSchool = "None"; // Replace with actual value from form field
        citation.setNotes(notesTextArea.getText());

        try {
            citation.updateCitation(); // Update the citation in the database
            System.out.println("Citation updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setEditableAll(false);
    }

    private boolean validSubmission() {
        boolean isValid = true;

        String regex1 = "\\d{4}-\\d{4}-\\d{4}-\\d{4}"; // ####-####-####-####
        String regex2 = "[A-Z]{4} \\d{3}"; // AAAA ###
        String regex3 = "[0-9A-Z]{17}"; // 2T2K1E56A12345674
        String regex4 = "^\\d+(\\.\\d{1,2})?$"; // ..###.##
        String regex5 = "^[A-Za-z'-]+(?:\\s+[A-Za-z'-]+)*$"; // Name Name

        // Driver's License Validation
        if (driverLicenseNumberTextField.getText() == null || driverLicenseNumberTextField.getText().isEmpty()
                || !driverLicenseNumberTextField.getText().matches(regex1)) {
            driverLicenseNumberErrorLabel.setText("*Invalid Input");
            driverLicenseNumberErrorLabel.setVisible(true);
            isValid = false;
        } else {
            driverLicenseNumberErrorLabel.setVisible(false);
        }

        // VIN Validation
        if (vehicleIdTextField.getText() == null || vehicleIdTextField.getText().isEmpty()
                || !vehicleIdTextField.getText().matches(regex3)) {
            vehicleIdErrorLabel.setText("*Invalid Input");
            vehicleIdErrorLabel.setVisible(true);
            isValid = false;
        } else {
            vehicleIdErrorLabel.setVisible(false);
        }

        // Fine Amount Validation
        if (fineAmountTextArea.getText() == null || fineAmountTextArea.getText().isEmpty()
                || !fineAmountTextArea.getText().matches(regex4)) {
            fineAmountErrorLabel.setText("*Invalid Input");
            fineAmountErrorLabel.setVisible(true);
            isValid = false;
        } else {
            fineAmountErrorLabel.setVisible(false);
        }

        // Officer Name Validation
        if (officerTextArea.getText() == null || officerTextArea.getText().isEmpty()
                || !officerTextArea.getText().matches(regex5)) {
            officerErrorLabel.setText("*Invalid Input");
            officerErrorLabel.setVisible(true);
            isValid = false;
        } else {
            officerErrorLabel.setVisible(false);
        }
        return isValid;
    }

    // Set all entries editable to true or false
    private void setEditableAll(boolean editable) {
        typeComboBox.setVisible(editable);
        typeLabel.setVisible(!editable);
        driverLicenseNumberTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        driverLicenseNumberTextField.setEditable(editable);
        vehicleIdTextField.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        vehicleIdTextField.setEditable(editable);
        fineAmountTextArea.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        fineAmountTextArea.setEditable(editable);
        officerTextArea.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        officerTextArea.setEditable(editable);
        paidRadioButton.setDisable(!editable);
        unpaidRadioButton.setDisable(!editable);
        notesTextArea.setStyle(editable ? defaultEntryStyle : uneditableEntryStyle);
        notesTextArea.setEditable(editable);
        applyButton.setVisible(editable);
        modifyButton.setVisible(!editable);
        cancelButton.setVisible(editable);
        deleteButton.setVisible(editable);
    }

    // Get Payment status selection
    private String getPaymentStatus() {
        if (paidRadioButton.isSelected()) {
            return "Paid";
        } else if (unpaidRadioButton.isSelected()) {
            return "Unpaid";
        } else {
            return null;
        }
    }

}
