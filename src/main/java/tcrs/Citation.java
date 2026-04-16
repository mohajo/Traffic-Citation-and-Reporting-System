package tcrs;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Class for citation creation
public class Citation {
	
//	Citation elements
    private int CitationID;
    private String Officer;
    private String Type;
    private String DLNumber;
    private String VIN;
    private LocalDateTime Date;
    private double FineAmount;
    private String PaymentStatus;
    private String TrafficSchool;
    private String Notes;

    // SQL Server and Credentials
    private static String url = "jdbc:mysql://localhost:3306/project?useSSL=false";
    private static String username = "root";
    private static String password = "test";

    // Citation Constructors
    public Citation() {
        // Default values for the citation attributes
        Officer = "";
        Type = "";
        DLNumber = "";
        VIN = "";
        Date = null;
        FineAmount = 0.0;
        PaymentStatus = "";
        TrafficSchool = "";
        Notes = "";
    }

    public Citation(String officer, String type, String dLNumber, String vIN, LocalDateTime date,
            double fineAmount, String paymentStatus, String trafficSchool, String notes) {
        Officer = officer;
        Type = type;
        DLNumber = dLNumber;
        VIN = vIN;
        Date = date;
        FineAmount = fineAmount;
        PaymentStatus = paymentStatus;
        TrafficSchool = trafficSchool;
        Notes = notes;
    }

    // Getters and Setters
    public int getCitationID() {
        return CitationID;
    }

    public void setCitationID(int citationID) {
        CitationID = citationID;
    }

    public String getOfficer() {
        return Officer;
    }

    public void setOfficer(String officer) {
        Officer = officer;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDLNumber() {
        return DLNumber;
    }

    public void setDLNumber(String dLNumber) {
        DLNumber = dLNumber;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String vIN) {
        VIN = vIN;
    }

    public LocalDateTime getDate() {
        return Date;
    }

    public void setDate(LocalDateTime date) {
        Date = date;
    }

    public double getFineAmount() {
        return FineAmount;
    }

    public void setFineAmount(double fineAmount) {
        FineAmount = fineAmount;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getTrafficSchool() {
        return TrafficSchool;
    }

    public void setTrafficSchool(String trafficSchool) {
        TrafficSchool = trafficSchool;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    // Save to an auto incrementing database
    public void save() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertCitationSql = "INSERT INTO citations (Officer, Type, DLNumber, VIN, Date, FineAmount, PaymentStatus, TrafficSchool, Notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertCitationSql);
            preparedStatement.setString(1, this.Officer);
            preparedStatement.setString(2, this.Type);
            preparedStatement.setString(3, this.DLNumber);
            preparedStatement.setString(4, this.VIN);
            preparedStatement.setObject(5, this.Date);
            preparedStatement.setDouble(6, this.FineAmount);
            preparedStatement.setString(7, this.PaymentStatus);
            preparedStatement.setString(8, this.TrafficSchool);
            preparedStatement.setString(9, this.Notes);

            preparedStatement.executeUpdate();
        }
    }

    // Update Citation
    public void updateCitation() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String updateCitationSql = "UPDATE citations SET Officer = ?, Type = ?, DLNumber = ?, VIN = ?, Date = ?, FineAmount = ?, PaymentStatus = ?, TrafficSchool = ?, Notes = ? WHERE CitationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateCitationSql);
            preparedStatement.setString(1, this.Officer);
            preparedStatement.setString(2, this.Type);
            preparedStatement.setString(3, this.DLNumber);
            preparedStatement.setString(4, this.VIN);
            preparedStatement.setObject(5, this.Date);
            preparedStatement.setDouble(6, this.FineAmount);
            preparedStatement.setString(7, this.PaymentStatus);
            preparedStatement.setString(8, this.TrafficSchool);
            preparedStatement.setString(9, this.Notes);
            preparedStatement.setInt(10, this.CitationID);

            preparedStatement.executeUpdate();
        }
    }

    // List all citations in the database
    public static List<Citation> getAllCitations() throws Exception {
        List<Citation> citations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectCitationsSql = "SELECT * FROM citations";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectCitationsSql);

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
        }

        return citations;
    }

    // Get next citation ID
    public static int getNextCitationID() {
        int maxCitationID = 0;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Create a statement to execute the query
            Statement statement = connection.createStatement();
            String query = "SELECT MAX(CitationID) as max_id FROM citations";
            ResultSet resultSet = statement.executeQuery(query);

            // Get the maximum citation ID and increment it by one
            if (resultSet.next()) {
                maxCitationID = resultSet.getInt("max_id");
            }

            // Close the database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxCitationID + 1;
    }

    // Search for a citation
    public static Citation searchCitation(int citationID) throws SQLException {
        Citation citation = null;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Create a PreparedStatement to search for the citation with the given citation ID
            String searchCitationSql = "SELECT * FROM citations WHERE CitationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(searchCitationSql);
            preparedStatement.setInt(1, citationID);

            // Execute the query and get the ResultSet of matching citations
            ResultSet resultSet = preparedStatement.executeQuery();

            // If a citation was found, create a Citation object and set its fields to the values from the ResultSet
            if (resultSet.next()) {
                String officer = resultSet.getString("Officer");
                String type = resultSet.getString("Type");
                String dlNumber = resultSet.getString("DLNumber");
                String vin = resultSet.getString("VIN");
                LocalDateTime date = resultSet.getTimestamp("Date").toLocalDateTime();
                double fineAmount = resultSet.getDouble("FineAmount");
                String paymentStatus = resultSet.getString("PaymentStatus");
                String trafficSchool = resultSet.getString("TrafficSchool");
                String notes = resultSet.getString("Notes");

                citation = new Citation(officer, type, dlNumber, vin, date, fineAmount, paymentStatus, trafficSchool,
                        notes);
                citation.setCitationID(citationID);
            }
        }

        return citation;
    }

    // Check if a citation Id exists
    public static boolean citationIdExists(int citationId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectSql = "SELECT COUNT(*) FROM citations WHERE CitationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setInt(1, citationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }

    // Check if driver's license number exists (required when making updates)
    public static boolean dLNumberExists(String dlNumber) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectSql = "SELECT COUNT(*) FROM drivers WHERE DLNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, dlNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }

    // Check if VIN exists (required when making updates)
    public static boolean vinExists(String vin) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT * FROM vehicles WHERE VIN = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, vin);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    // Delete a citation
    public void deleteCitation() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String deleteCitationSql = "DELETE FROM citations WHERE CitationID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteCitationSql);
            preparedStatement.setInt(1, this.CitationID);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public String toString() {
        return "Citation{" +
                "CitationID=" + CitationID +
                ", Officer='" + Officer + '\'' +
                ", Type='" + Type + '\'' +
                ", DLNumber='" + DLNumber + '\'' +
                ", VIN='" + VIN + '\'' +
                ", Date=" + Date +
                ", FineAmount=" + FineAmount +
                ", PaymentStatus='" + PaymentStatus + '\'' +
                ", TrafficSchool='" + TrafficSchool + '\'' +
                ", Notes='" + Notes + '\'' +
                '}';
    }
}
