package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

//Controller class for searching records screen
public class SearchAllController {
	
//	JavaFX screen elements
    @FXML private TextField citationIDTextField;
    @FXML private Label citationIDErrorLabel;
    @FXML private Button citationIDSearch;
    @FXML private TextField driverLicenseNumberTextField;
    @FXML private Label driverLicenseNumberErrorLabel;
    @FXML private Button driverLicenseNumberSearch;
    @FXML private TextField vehicleIdTextField;
    @FXML private Label vehicleIdErrorLabel;
    @FXML private Button vehicleIdSearch;

//  Methods to implement screen elements actions
    @FXML
    public void switchToHome() throws IOException {
        App.setRoot("DashboardLocal");
    }

    @FXML 
    public void switchToSearch() throws IOException {
        App.setRoot("SearchAll");
    }

    @FXML
    public void switchToReports() throws IOException {
        App.setRoot("ReportLocal");
    }

    @FXML
    public void switchToLogout() throws IOException {
        App.setRoot("Login");
    }
    
    @FXML
    private void handleCitaionIDSearchButton() throws IOException, SQLException {
        // code to handle search by Citation ID
        String regex = "\\d{8}"; // ########
        if (citationIDTextField.getText() == null || citationIDTextField.getText().isEmpty()
                || !citationIDTextField.getText().matches(regex)) {
            citationIDErrorLabel.setText("*Invalid Input");
            citationIDErrorLabel.setVisible(true);
        } else {
            citationIDErrorLabel.setVisible(false);
            if (!Citation.citationIdExists(Integer.parseInt(citationIDTextField.getText()))) {
                citationIDErrorLabel.setText("*Citation does not exist");
                citationIDErrorLabel.setVisible(true);
                System.out.println("Citation does not exist");
                return;
            }
            try {
                int citationIdInt = Integer.parseInt(citationIDTextField.getText());
                Citation citation = Citation.searchCitation(citationIdInt);
                if (citation != null) {
                    System.out.println("Citation found \n" + citation.toString());
                    Load load = new Load(); // Get the current stage and pass it to the modifyCitation method
                    Stage currentStage = (Stage) citationIDTextField.getScene().getWindow();
                    load.modifyCitation(citationIdInt, currentStage);
                } 
//                else	
//                    System.out.println("Citation " + citationIdInt + " Not Found");
//                    citationIDErrorLabel.setText("*Citation not found");
//                    citationIDErrorLabel.setVisible(true);
            } catch (SQLException e) {
                System.out.println("Error searching for citation");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDLNumberSearchtButton() {
        // code to handle search by DL number
        String regex = "\\d{4}-\\d{4}-\\d{4}-\\d{4}"; // ####-####-####-####
        if (driverLicenseNumberTextField.getText() == null || driverLicenseNumberTextField.getText().isEmpty()
                || !driverLicenseNumberTextField.getText().matches(regex)) {
            driverLicenseNumberErrorLabel.setText("*Invalid Input");
            driverLicenseNumberErrorLabel.setVisible(true);
        } else {
            driverLicenseNumberErrorLabel.setVisible(false);
            String dLNumber = driverLicenseNumberTextField.getText();
            try {
                Driver driver = Driver.searchDriver(dLNumber);
                if (driver != null) {
                    System.out.println("Driver found \n" + driver.toString());
                    Load load = new Load(); // Get the current stage and pass it to the modifyVehicle method
                    Stage currentStage = (Stage) driverLicenseNumberTextField.getScene().getWindow();
                    load.modifyDriver(dLNumber, currentStage);
                } else {
                    System.out.println("Driver " + driver + " Not Found");
                    driverLicenseNumberErrorLabel.setText("*Driver not found");
                    driverLicenseNumberErrorLabel.setVisible(true);
                }
            } catch (SQLException | IOException e) {
                System.out.println("Error searching for driver");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleVINSearchButton() {
        // code to handle search by VIN
        String regex = "[0-9A-Z]{17}"; // 2T2K1E56A12345674

        if (vehicleIdTextField.getText() == null || vehicleIdTextField.getText().isEmpty()
                || !vehicleIdTextField.getText().matches(regex)) {
            vehicleIdErrorLabel.setText("*Invalid Input");
            vehicleIdErrorLabel.setVisible(true);
        } else {
            vehicleIdErrorLabel.setVisible(false);
            String vin = vehicleIdTextField.getText();
            try {
                Vehicle vehicle = Vehicle.searchVehicle(vin);
                if (vehicle != null) {
                    System.out.println("Vehicle found \n" + vehicle.toString());
                    Load load = new Load(); // Get the current stage and pass it to the modifyVehicle method
                    Stage currentStage = (Stage) vehicleIdTextField.getScene().getWindow();
                    load.modifyVehicle(vin, currentStage);
                } else {
                    System.out.println("Vehicle " + vin + " Not Found");
                    vehicleIdErrorLabel.setText("*Vehicle not found");
                    vehicleIdErrorLabel.setVisible(true);
                }
            } catch (SQLException | IOException e) {
                System.out.println("Error searching for vehicle");
                e.printStackTrace();
            }
        }
    }
}
