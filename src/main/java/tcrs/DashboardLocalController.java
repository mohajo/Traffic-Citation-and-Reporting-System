package tcrs;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import java.time.format.FormatStyle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

//Controller class for the JavaFX Local scene
public class DashboardLocalController implements Initializable {

//	JavaFX scene elements
    @FXML private TableView<Citation> citationTable;
    @FXML private TableColumn<Citation, LocalDateTime> dateIssuedColumn;
    @FXML private TableColumn<Citation, String> typeColumn;
    @FXML private TableColumn<Citation, String> paymentStatusColumn;
    @FXML private ComboBox<String> itemTypeComboBox;
    @FXML private TableColumn<Citation, String> issuedByColumn;
    @FXML private TableColumn<Citation, Integer> citationIdColumn;
    @FXML private TableView<Driver> driverTable;
    @FXML private TableColumn<Driver, String> dLColumn;
    @FXML private TableColumn<Driver, String> firstNameColumn;
    @FXML private TableColumn<Driver, String> lastNameColumn;
    @FXML private TableColumn<Driver, String> licenseColumn;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> makeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> yearColumn;
    @FXML private TableColumn<Vehicle, String> vINColumn;
    @FXML private TableColumn<Vehicle, String> registrationStatusColumn;
    @FXML private Button refreshButton;
    @FXML private Label welcomeTitle;

//  Method to initialize the controller 
    public void initialize(URL location, ResourceBundle resources) {

        welcomeTitle.setText(welcomeTitle.getText() + " " + AuthController.user.getUsername());

        // Combo Box
        itemTypeComboBox.getItems().addAll("Citation", "Driver", "Vehicle");
        itemTypeComboBox.setOnAction(event -> {
            String selectedItemType = itemTypeComboBox.getValue();
            if (selectedItemType != null) {
                Load load = new Load();
                switch (selectedItemType) {
                    case "Citation":
                        try {
                            load.newCitation();
                            itemTypeComboBox.setPromptText("Create New");
                            itemTypeComboBox.setValue(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Vehicle":
                        try {
                            load.newVehicle();
                            itemTypeComboBox.setPromptText("Create New");
                            itemTypeComboBox.setValue(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Driver":
                        try {
                            load.newDriver();
                            itemTypeComboBox.setPromptText("Create New");
                            itemTypeComboBox.setValue(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        // set up columns
        citationIdColumn.setCellValueFactory(new PropertyValueFactory<>("citationID"));
        citationIdColumn.setCellFactory(column -> new TextFieldTableCell<>(new StringConverter<>() {
            @Override
            public String toString(Integer integer) {
                return String.format("%08d", integer);
            }
        
            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        dateIssuedColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));
        // Define the date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        // Set up custom cell factory for the dateIssuedColumn
        dateIssuedColumn.setCellFactory(column -> new TableCell<Citation, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        issuedByColumn.setCellValueFactory(new PropertyValueFactory<>("officer"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // set up columns for Drivers
        dLColumn.setCellValueFactory(new PropertyValueFactory<>("dLNumber"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        licenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseStatus"));

        // set up columns for vehicleTable
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        vINColumn.setCellValueFactory(new PropertyValueFactory<>("VIN"));
        registrationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("registrationStatus"));

        try {
            // get all citations from the database
            List<Citation> citations = Citation.getAllCitations();

            // populate the table with the citations
            citationTable.getItems().addAll(citations);

            // get all drivers from database
            List<Driver> drivers = Driver.getAllDrivers();

            // populate the table with the citations
            driverTable.getItems().addAll(drivers);

            // get all vehicles from the database
            List<Vehicle> vehicles = Vehicle.getAllVehicles();

            // populate the vehicleTable with the vehicles
            vehicleTable.getItems().addAll(vehicles);

            // add listener for double click
            citationTable.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Citation citation = citationTable.getSelectionModel().getSelectedItem();
                    try {
                        if (citation != null && Citation.citationIdExists(citation.getCitationID())) {
                            Load load = new Load();
                            Node node = (Node) event.getSource();
                            Stage currentStage = (Stage) node.getScene().getWindow();

                            load.modifyCitation(citation.getCitationID(), currentStage);
                        } else {
                            refreshTable();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // add listener for double click
            driverTable.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Driver driver = driverTable.getSelectionModel().getSelectedItem();
                    try {
                        if (driver != null && Driver.searchDriver(driver.getDLNumber()) != null) {
                            Load load = new Load();
                            Node node = (Node) event.getSource();
                            Stage currentStage = (Stage) node.getScene().getWindow();

                            load.modifyDriver(driver.getDLNumber(), currentStage);
                        } else {
                            refreshTable();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // add listener for double click on vehicleTable
            vehicleTable.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Vehicle vehicle = vehicleTable.getSelectionModel().getSelectedItem();
                    try {
                        if (vehicle != null && Vehicle.searchVehicle(vehicle.getVIN()) != null) {
                            Load load = new Load();
                            Node node = (Node) event.getSource();
                            Stage currentStage = (Stage) node.getScene().getWindow();

                            load.modifyVehicle(vehicle.getVIN(), currentStage);
                        } else {
                            refreshTable();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    Methods to implement scene elements actions
    @FXML
    private void handleRefreshButton() throws Exception {
        refreshTable();
    }
    
    private void refreshTable() throws Exception {
        // Clear current data in the tables
        citationTable.getItems().clear();
        driverTable.getItems().clear();
        vehicleTable.getItems().clear();

        // Retrieve new data from the database
        List<Citation> citations = Citation.getAllCitations();
        List<Driver> drivers = Driver.getAllDrivers();
        List<Vehicle> vehicles = Vehicle.getAllVehicles();

        // Populate the tables with the new data
        citationTable.getItems().addAll(citations);
        driverTable.getItems().addAll(drivers);
        vehicleTable.getItems().addAll(vehicles);
    }

    @FXML
    public void switchToHome() throws IOException {
        App.setRoot("DashboardLocal");
    }

    @FXML void switchToReports() throws IOException {
        App.setRoot("ReportLocal");
    }

    @FXML void switchToSearch() throws IOException {
        App.setRoot("SearchAll");
    }

    @FXML void switchToLogout() throws IOException {
        App.setRoot("Login");
    }
}
