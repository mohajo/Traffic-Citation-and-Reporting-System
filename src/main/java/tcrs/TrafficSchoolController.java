package tcrs;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

//Controller class for traffic school screen
public class TrafficSchoolController {
    
//	JavaFX screen elements
	@FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label dLNumberLabel;
    @FXML private Label submitErrorLabel;
    @FXML private Button SubmitButton;
    @FXML private DatePicker sessionDatePicker0;
    @FXML private TextField sessionDurationTextField0;
    @FXML private Label sessionErrorLabel0;
    @FXML private HBox sessionHBox1;
    @FXML private DatePicker sessionDatePicker1;
    @FXML private Button sessionAddButton1;
    @FXML private TextField sessionDurationTextField1;
    @FXML private Label sessionErrorLabel1;
    @FXML private HBox sessionHBox2;
    @FXML private DatePicker sessionDatePicker2;
    @FXML private TextField sessionDurationTextField2;
    @FXML private Label sessionErrorLabel2;
    @FXML private Button sessionAddButton2;
    @FXML private TextArea reportNotes;
    @FXML private Button cancelButton;

    private Citation citation;
    private Driver driver;

    private boolean added1 = false;
    private boolean added2 = false;

    private String defaultStyleDP;

//  Method to initialize the controller
    public void initialize(int currCitationID, String currDriver) {
        // get default style for date picker
        defaultStyleDP = sessionDatePicker0.getStyle();

        sessionHBox1.setVisible(false);
        sessionHBox2.setVisible(false);
        sessionAddButton2.setVisible(false);
        submitErrorLabel.setVisible(false);
        sessionErrorLabel0.setVisible(false);
        sessionErrorLabel1.setVisible(false);
        sessionErrorLabel2.setVisible(false);

        // Populate page with current citation values
        try {
            citation = Citation.searchCitation(currCitationID);
            driver = Driver.searchDriver(currDriver);
            System.out.println(citation);
            firstNameLabel.setText(driver.getFirstName()); // replace with getting firstname from drvier class
            lastNameLabel.setText(driver.getLastName()); // replace with getting lastname from driver class
            dLNumberLabel.setText(citation.getDLNumber());

            // Sessions and notes
            TrafficSchoolInfoExtractor extractor = new TrafficSchoolInfoExtractor(citation.getTrafficSchool());
            List<TrafficSchoolInfoExtractor.SessionInfo> sessions = extractor.getSessions();
            String additionalNotes = extractor.getAdditionalNotes();
            if (extractor.getLength() > 0) {
                sessionDatePicker0.setValue(sessions.get(0).getSessionDate());
                sessionDurationTextField0.setText(Integer.toString(sessions.get(0).getSessionDuration()));
            }
            if (extractor.getLength() > 1) {
                handleSessionAddButton1();
                sessionDatePicker1.setValue(sessions.get(1).getSessionDate());
                sessionDurationTextField1.setText(Integer.toString(sessions.get(1).getSessionDuration()));
            }
            if (extractor.getLength() > 2) {
                handleSessionAddButton2();
                sessionDatePicker2.setValue(sessions.get(2).getSessionDate());
                sessionDurationTextField2.setText(Integer.toString(sessions.get(2).getSessionDuration()));
            }
            reportNotes.setText(additionalNotes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//  Methods to implement screen elements actions
    @FXML
    private void handleCancelButton() {
        // get a reference to the stage
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        // close the stage
        stage.close();
    }

    @FXML
    private void handleSessionAddButton1() {
        // toggle visibility
        sessionHBox1.setVisible(!added1);
        sessionAddButton2.setVisible(!added1);
        sessionAddButton1.setText(added1 ? "Add" : "Remove");

        // reset inputs
        sessionDatePicker1.setValue(null);
        sessionDatePicker1.setStyle(defaultStyleDP);
        sessionDurationTextField1.setText("");
        sessionErrorLabel1.setVisible(false);

        added1 = !added1;
    }

    @FXML
    private void handleSessionAddButton2() {
        sessionHBox2.setVisible(!added2);
        sessionAddButton1.setVisible(added2);
        sessionAddButton2.setText(added2 ? "Add" : "Remove");

        // reset inputs
        sessionDatePicker2.setValue(null);
        sessionDatePicker2.setStyle(defaultStyleDP);
        sessionDurationTextField2.setText("");
        sessionErrorLabel2.setVisible(false);

        added2 = !added2;
    }

    @FXML
    private void handleSubmitButton() throws SQLException {
        boolean valid = true;
        // Validate inputs
        String regex = "^\\d+(\\.\\d{1,2})?$";
        sessionErrorLabel0.setVisible(!sessionDurationTextField0.getText().matches(regex));
        sessionErrorLabel1.setVisible(!sessionDurationTextField1.getText().matches(regex) && added1);
        sessionErrorLabel2.setVisible(!sessionDurationTextField2.getText().matches(regex) && added2);

        if (sessionErrorLabel0.isVisible() || sessionErrorLabel1.isVisible() || sessionErrorLabel2.isVisible()) {
            System.out.println("Invalid duration inputs");
            valid = false;
        }

        sessionDatePicker0
                .setStyle(sessionDatePicker0.getValue() == null ? "-fx-background-color: red" : defaultStyleDP);
        sessionDatePicker1
                .setStyle(
                        sessionDatePicker1.getValue() == null & added1 ? "-fx-background-color: red" : defaultStyleDP);
        sessionDatePicker2
                .setStyle(
                        sessionDatePicker2.getValue() == null & added2 ? "-fx-background-color: red" : defaultStyleDP);

        LocalDate session0Date = sessionDatePicker0.getValue();
        LocalDate session1Date = sessionDatePicker1.getValue();
        LocalDate session2Date = sessionDatePicker2.getValue();

        if (session0Date == null || (session1Date == null && added1) || (session2Date == null && added2)) {
            System.out.println("Invalid session inputs");
            valid = false;
        }

        // Code to execute if valid
        if (!valid) {
            return;
        } else {
            double session0Duration = Double.parseDouble(sessionDurationTextField0.getText());
            double session1Duration = !sessionDurationTextField1.getText().isEmpty()
                    ? Double.parseDouble(sessionDurationTextField1.getText())
                    : 0;
            double session2Duration = !sessionDurationTextField2.getText().isEmpty()
                    ? Double.parseDouble(sessionDurationTextField2.getText())
                    : 0;
            double totalDuration = session0Duration + session1Duration + session2Duration;
            System.out.println("Total Duration: " + totalDuration);
            if (totalDuration < 8) {
                submitErrorLabel.setVisible(true);
            } else {
                submitErrorLabel.setVisible(false);
                // Build Traffic School Information as String
                System.out.println(trafficSchoolString());
                citation.setTrafficSchool(trafficSchoolString());
                citation.updateCitation();
                // get a reference to the stage
                Stage stage = (Stage) SubmitButton.getScene().getWindow();
                // close the stage
                stage.close();
            }
        }
    }

    // Build traffic school information into one string to store to database
    private String trafficSchoolString() {
        StringBuilder trafficSchoolString = new StringBuilder();

        // Session 0 information
        trafficSchoolString.append("Session 0: ");
        trafficSchoolString.append("Date: ").append(sessionDatePicker0.getValue());
        trafficSchoolString.append(", Duration: ").append(sessionDurationTextField0.getText()).append(" hours");

        // Session 1 information (if added)
        if (added1) {
            trafficSchoolString.append("\nSession 1: ");
            trafficSchoolString.append("Date: ").append(sessionDatePicker1.getValue());
            trafficSchoolString.append(", Duration: ").append(sessionDurationTextField1.getText()).append(" hours");
        }

        // Session 2 information (if added)
        if (added2) {
            trafficSchoolString.append("\nSession 2: ");
            trafficSchoolString.append("Date: ").append(sessionDatePicker2.getValue());
            trafficSchoolString.append(", Duration: ").append(sessionDurationTextField2.getText()).append(" hours");
        }

        // Additional notes
        if (reportNotes.getText() != null) {
            trafficSchoolString.append("\nAdditional Notes: ");
            trafficSchoolString.append(reportNotes.getText());
        }

        return trafficSchoolString.toString();
    }
}
