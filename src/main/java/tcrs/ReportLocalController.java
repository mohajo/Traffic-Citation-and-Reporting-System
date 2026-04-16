package tcrs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import static tcrs.AuthController.user;


//Controller class for report screen for local users
public class ReportLocalController {
	
//	JavaFX screen elements	
    @FXML Label currentDate;
    @FXML DatePicker startDate;
    @FXML DatePicker endDate;
    @FXML TextArea notes;
    @FXML TextField nameTextField;
    @FXML RadioButton yesWarrant;
    @FXML RadioButton noWarrant;
    @FXML TextField driverTextField;
    @FXML TextField licenseTextField;
    
    PreparedStatement preparedStatement = null;
    
    // SQL Server and Credentials
    private static String url = "jdbc:mysql://localhost:3306/project?useSSL=false";
    private static String username = "root";
    private static String password = "test";

//  Method to initialize the controller
    public void initialize () {
        currentDate.setText("Current Report: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
//  Methods to implement screen elements actions
    @FXML
    public void switchToSearch() throws IOException {
        App.setRoot("SearchAll");
    }

    @FXML
    public void switchToLogout() throws IOException{
        App.setRoot("Login");
    }

    @FXML
    public void switchToReports() throws IOException {
        App.setRoot("ReportLocal");
    }

    //Submit button clicked - generate report
    @FXML
    public void switchToDashboard() throws IOException {
        App.setRoot("DashboardLocal");
    }

    public void getCitationReport() throws Exception {
        List<Citation> citations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectCitationsSql = "SELECT * FROM citations where Date >= ? and Date <= ?";

            if (nameTextField != null && !nameTextField.getText().trim().isEmpty()) {
                selectCitationsSql = "SELECT * FROM citations where Date >= ? and Date <= ? and Officer = ?";
                preparedStatement = connection.prepareStatement(selectCitationsSql);
                preparedStatement.setString(1, String.valueOf(startDate.getValue()));
                preparedStatement.setString(2, String.valueOf(endDate.getValue()));
                preparedStatement.setString(3, nameTextField.getText());
            } else {

                preparedStatement = connection.prepareStatement(selectCitationsSql);
                preparedStatement.setString(1, String.valueOf(startDate.getValue()));
                preparedStatement.setString(2, String.valueOf(endDate.getValue()));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int citationId = resultSet.getInt("CitationID");
                String officer = resultSet.getString("Officer");
                String type = resultSet.getString("Type");
                String dlNumber = resultSet.getString("DLNumber");
                String vin = resultSet.getString("VIN");
                LocalDateTime date = resultSet.getTimestamp("Date").toLocalDateTime();
                double fineAmount = resultSet.getDouble("FineAmount");
                String paymentStatus = resultSet.getString("PaymentStatus");
                String trafficSchool = resultSet.getString("TrafficSchool");
                String notes = resultSet.getString("Notes");

                Citation citation = new Citation(officer, type, dlNumber, vin, date, fineAmount, paymentStatus,
                        trafficSchool, notes);
                citation.setCitationID(citationId);
                citations.add(citation);
            }

            exportToExcel(citations);
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Report status");
        alert.setHeaderText("Report downloaded successfully");
        alert.showAndWait();
    }

    public void exportToExcel(List<Citation> citations) throws IOException {
    	LocalDateTime date = LocalDateTime.now();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        final String formatTemplate = "{0},{1},{2},{3},{4},{5},{6},{7},{8},{9}";
        FileWriter writer = new FileWriter("src/main/util/Report" + date.format(formatter) + ".csv");
        BufferedWriter bw = new BufferedWriter(writer);
        
        final String headerString = MessageFormat.format(formatTemplate, "Id"
                , "Type"
                , "Officer"
                , "DL Number"
                , "VIN"
                , "Date"
                , "Fine Amount"
                , "Payment Status"
                , "Traffic School"
                , "Notes");

        bw.write(headerString);
        bw.newLine();

        final int size = citations.size();

        for (int i = 0; i < size; i++) {
            final String lineString = MessageFormat.format(formatTemplate, citations.get(i).getCitationID()
                    , citations.get(i).getType()
                    , citations.get(i).getOfficer()
                    , citations.get(i).getDLNumber()
                    , citations.get(i).getVIN()
                    , citations.get(i).getDate()
                    , citations.get(i).getFineAmount()
                    , citations.get(i).getPaymentStatus()
                    , citations.get(i).getTrafficSchool()
                    , citations.get(i).getNotes());

            bw.write(lineString);
            bw.newLine();
        }
        bw.close();
        writer.close();
    }
}
