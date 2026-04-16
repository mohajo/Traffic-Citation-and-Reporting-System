package tcrs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Class for vehicle creation
public class Vehicle {
	
//	Vehicle elements
    private String VIN;
    private String LicensePlateNumber;
    private int Year;
    private String Make;
    private String Model;
    private String Color;
    private boolean RegistrationStatus;
    private boolean StolenStatus;
    private boolean WarrantStatus;
    private String Notes;

    // SQL Server and Credentials
    private static String url = "jdbc:mysql://localhost:3306/project?useSSL=false";
    private static String username = "root";
    private static String password = "test";

    // Constructors
    public Vehicle() {
        // Default values for the vehicle attributes
        LicensePlateNumber = "";
        Year = 0;
        Make = "";
        Model = "";
        Color = "";
        RegistrationStatus = false;
        StolenStatus = false;
        WarrantStatus = false;
        Notes = "";
    }

    public Vehicle(String VIN, String licensePlateNumber, int year, String make, String model, String color, boolean registrationStatus,
            boolean stolenStatus, boolean warrantStatus, String notes) {
        this.VIN = VIN;
        this.LicensePlateNumber = licensePlateNumber;
        this.Year = year;
        this.Make = make;
        this.Model = model;
        this.Color = color;
        this.RegistrationStatus = registrationStatus;
        this.StolenStatus = stolenStatus;
        this.WarrantStatus = warrantStatus;
        this.Notes = notes;
    }

    // Getters and Setters
    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        this.Year = year;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        this.Color = color;
    }

    public String getLicensePlateNumber() {
        return LicensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.LicensePlateNumber = licensePlateNumber;
    }

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        this.Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        this.Model = model;
    }

    public boolean isRegistrationStatus() {
        return RegistrationStatus;
    }

    public void setRegistrationStatus(boolean registrationStatus) {
        this.RegistrationStatus = registrationStatus;
    }

    public boolean isStolenStatus() {
        return StolenStatus;
    }

    public void setStolenStatus(boolean stolenStatus) {
        this.StolenStatus = stolenStatus;
    }

    public boolean isWarrantStatus() {
        return WarrantStatus;
    }

    public void setWarrantStatus(boolean warrantStatus) {
        this.WarrantStatus = warrantStatus;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        this.Notes = notes;
    }

    // Methods
    // Save to the database
    public void save() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertVehicleSql = "INSERT INTO Vehicles (VIN, LicensePlateNumber, Year, Make, Model, Color, RegistrationStatus, StolenStatus, WarrantStatus, Notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertVehicleSql);
            preparedStatement.setString(1, this.VIN);
            preparedStatement.setString(2, this.LicensePlateNumber);
            preparedStatement.setInt(3, this.Year);
            preparedStatement.setString(4, this.Make);
            preparedStatement.setString(5, this.Model);
            preparedStatement.setString(6, this.Color);
            preparedStatement.setBoolean(7, this.RegistrationStatus);
            preparedStatement.setBoolean(8, this.StolenStatus);
            preparedStatement.setBoolean(9, this.WarrantStatus);
            preparedStatement.setString(10, this.Notes);

            preparedStatement.executeUpdate();
        }
    }

    // Update Vehicle
    public void updateVehicle() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String updateVehicleSql = "UPDATE Vehicles SET LicensePlateNumber = ?, Year = ?, Make = ?, Model = ?, Color = ?, RegistrationStatus = ?, StolenStatus = ?, WarrantStatus = ?, Notes = ? WHERE VIN = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateVehicleSql);
            preparedStatement.setString(1, this.LicensePlateNumber);
            preparedStatement.setInt(2, this.Year);
            preparedStatement.setString(3, this.Make);
            preparedStatement.setString(4, this.Model);
            preparedStatement.setString(5, this.Color);
            preparedStatement.setBoolean(6, this.RegistrationStatus);
            preparedStatement.setBoolean(7, this.StolenStatus);
            preparedStatement.setBoolean(8, this.WarrantStatus);
            preparedStatement.setString(9, this.Notes);
            preparedStatement.setString(10, this.VIN);

            preparedStatement.executeUpdate();
        }
    }

    // List all vehicles in the database
    public static List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectVehiclesSql = "SELECT * FROM Vehicles";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectVehiclesSql);

            while (resultSet.next()) {
                String vin = resultSet.getString("VIN");
                String licensePlateNumber = resultSet.getString("LicensePlateNumber");
                int year = resultSet.getInt("Year");
                String make = resultSet.getString("Make");
                String model = resultSet.getString("Model");
                String color = resultSet.getString("Color");
                boolean registrationStatus = resultSet.getBoolean("RegistrationStatus");
                boolean stolenStatus = resultSet.getBoolean("StolenStatus");
                boolean warrantStatus = resultSet.getBoolean("WarrantStatus");
                String notes = resultSet.getString("Notes");

                Vehicle vehicle = new Vehicle(vin, licensePlateNumber, year, make, model, color, registrationStatus, stolenStatus,
                        warrantStatus, notes);
                vehicles.add(vehicle);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return vehicles;
    }

    // Search for a vehicle
    public static Vehicle searchVehicle(String vin) throws SQLException {
        Vehicle vehicle = null;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Create a PreparedStatement to search for the vehicle with the given VIN
            String searchVehicleSql = "SELECT * FROM Vehicles WHERE VIN = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(searchVehicleSql);
            preparedStatement.setString(1, vin);

            // Execute the query and get the ResultSet of matching vehicles
            ResultSet resultSet = preparedStatement.executeQuery();

            // If a vehicle was found, create a Vehicle object and set its fields to the
            // values from the ResultSet
            if (resultSet.next()) {
                String licensePlateNumber = resultSet.getString("LicensePlateNumber");
                int year = resultSet.getInt("Year");
                String make = resultSet.getString("Make");
                String model = resultSet.getString("Model");
                String color = resultSet.getString("Color");
                boolean registrationStatus = resultSet.getBoolean("RegistrationStatus");
                boolean stolenStatus = resultSet.getBoolean("StolenStatus");
                boolean warrantStatus = resultSet.getBoolean("WarrantStatus");
                String notes = resultSet.getString("Notes");

                vehicle = new Vehicle(vin, licensePlateNumber, year, make, model, color, registrationStatus, stolenStatus,
                        warrantStatus, notes);
            }
        }

        return vehicle;
    }

    // Delete this vehicle
    public void deleteVehicle() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String deleteVehicleSql = "DELETE FROM Vehicles WHERE VIN = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteVehicleSql);
            preparedStatement.setString(1, this.VIN);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "VIN='" + VIN + '\'' +
                ", licensePlateNumber='" + LicensePlateNumber + '\'' +
                ", make='" + Make + '\'' +
                ", model='" + Model + '\'' +
                ", registrationStatus=" + RegistrationStatus +
                ", stolenStatus=" + StolenStatus +
                ", warrantStatus=" + WarrantStatus +
                ", notes='" + Notes + '\'' +
                '}';
    }
}
