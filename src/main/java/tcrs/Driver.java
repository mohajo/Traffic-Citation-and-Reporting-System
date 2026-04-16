package tcrs;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Class for driver creation
public class Driver {
	
//	Driver info elements
    private String DLNumber;
    private String FirstName;
    private String LastName;
    private LocalDate DateOfBirth;
    private String LicenseStatus;
    private boolean WarrantStatus;
    private String DrivingRecord;
    private String Notes;

    // SQL Server and Credentials
    private static String url = "jdbc:mysql://localhost:3306/project?useSSL=false";
    private static String username = "root";
    private static String password = "test";

    // Constructors
    public Driver() {
        // Default values for the driver attributes
        DLNumber = "";
        FirstName = "";
        LastName = "";
        DateOfBirth = null;
        LicenseStatus = "";
        WarrantStatus = false;
        DrivingRecord = "";
        Notes = "";
    }

    public Driver(String DLNumber, String FirstName, String LastName, LocalDate DateOfBirth,
            String LicenseStatus, boolean WarrantStatus, String DrivingRecord, String Notes) {
        this.DLNumber = DLNumber;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.DateOfBirth = DateOfBirth;
        this.LicenseStatus = LicenseStatus;
        this.WarrantStatus = WarrantStatus;
        this.DrivingRecord = DrivingRecord;
        this.Notes = Notes;
    }

    // Getters and Setters
    public String getDLNumber() {
        return DLNumber;
    }

    public void setDLNumber(String DLNumber) {
        this.DLNumber = DLNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getLicenseStatus() {
        return LicenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        LicenseStatus = licenseStatus;
    }

    public boolean isWarrantStatus() {
        return WarrantStatus;
    }

    public void setWarrantStatus(boolean warrantStatus) {
        WarrantStatus = warrantStatus;
    }

    public String getDrivingRecord() {
        return DrivingRecord;
    }

    public void setDrivingRecord(String drivingRecord) {
        DrivingRecord = drivingRecord;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    // Save driver to the database
    public void save() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertDriverSql = "INSERT INTO Drivers (DLNumber, FirstName, LastName, DateOfBirth, LicenseStatus, WarrantStatus, DrivingRecord, Notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDriverSql);
            preparedStatement.setString(1, this.DLNumber);
            preparedStatement.setString(2, this.FirstName);
            preparedStatement.setString(3, this.LastName);
            preparedStatement.setObject(4, this.DateOfBirth);
            preparedStatement.setString(5, this.LicenseStatus);
            preparedStatement.setBoolean(6, this.WarrantStatus);
            preparedStatement.setString(7, this.DrivingRecord);
            preparedStatement.setString(8, this.Notes);
            preparedStatement.executeUpdate();
        }
    }

    // Update driver in the database
    public void updateDriver() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String updateDriverSql = "UPDATE Drivers SET FirstName = ?, LastName = ?, DateOfBirth = ?, LicenseStatus = ?, WarrantStatus = ?, DrivingRecord = ?, Notes = ? WHERE DLNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateDriverSql);
            preparedStatement.setString(1, this.FirstName);
            preparedStatement.setString(2, this.LastName);
            preparedStatement.setObject(3, this.DateOfBirth);
            preparedStatement.setString(4, this.LicenseStatus);
            preparedStatement.setBoolean(5, this.WarrantStatus);
            preparedStatement.setString(6, this.DrivingRecord);
            preparedStatement.setString(7, this.Notes);
            preparedStatement.setString(8, this.DLNumber);

            preparedStatement.executeUpdate();
        }
    }

    // Delete driver from the database
    public void deleteDriver() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String deleteDriverSql = "DELETE FROM Drivers WHERE DLNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteDriverSql);
            preparedStatement.setString(1, this.DLNumber);

            preparedStatement.executeUpdate();
        }
    }

    // Get all drivers from the database
    public static List<Driver> getAllDrivers() throws SQLException {
        List<Driver> drivers = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectDriversSql = "SELECT * FROM Drivers";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectDriversSql);

            while (resultSet.next()) {
                String dlNumber = resultSet.getString("DLNumber");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                LocalDate dateOfBirth = resultSet.getDate("DateOfBirth").toLocalDate();
                String licenseStatus = resultSet.getString("LicenseStatus");
                boolean warrantStatus = resultSet.getBoolean("WarrantStatus");
                String drivingRecord = resultSet.getString("DrivingRecord");
                String notes = resultSet.getString("Notes");

                Driver driver = new Driver(dlNumber, firstName, lastName, dateOfBirth, licenseStatus, warrantStatus,
                        drivingRecord, notes);
                drivers.add(driver);
            }
        }

        return drivers;
    }

    // Search for a driver by driver's license number
    public static Driver searchDriver(String dlNumber) throws SQLException {
        Driver driver = null;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String searchDriverSql = "SELECT * FROM Drivers WHERE DLNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(searchDriverSql);
            preparedStatement.setString(1, dlNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                LocalDate dateOfBirth = resultSet.getDate("DateOfBirth").toLocalDate();
                String licenseStatus = resultSet.getString("LicenseStatus");
                boolean warrantStatus = resultSet.getBoolean("WarrantStatus");
                String drivingRecord = resultSet.getString("DrivingRecord");
                String notes = resultSet.getString("Notes");

                driver = new Driver(dlNumber, firstName, lastName, dateOfBirth, licenseStatus, warrantStatus,
                        drivingRecord, notes);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return driver;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "DLNumber='" + DLNumber + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", DateOfBirth=" + DateOfBirth +
                ", LicenseStatus='" + LicenseStatus + '\'' +
                ", WarrantStatus=" + WarrantStatus +
                ", DrivingRecord='" + DrivingRecord + '\'' +
                ", Notes='" + Notes + '\'' +
                '}';
    }
}