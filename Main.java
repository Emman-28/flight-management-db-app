import java.sql.*;
import java.util.Scanner;
import operations.*;
import GUI.*;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Establishing a connection
            connection = DatabaseConnection.connect();
            System.out.println("Connected to the database successfully!");
            System.out.println("\nWelcome to the Flight Database Management System!\n");

            // records management
            ManageRecord record = new ManageRecord(connection);
            AircraftManager aircraftManager = new AircraftManager(connection);
            FlightManager flightManager = new FlightManager(connection);
            FlightLogManager flightLogManager = new FlightLogManager(connection);

            // transactions
            ExecuteTransaction transaction = new ExecuteTransaction(connection);

            // report generation
            GenerateReport report = new GenerateReport(connection);
            
            // Pass the connection and managers to the GUI
            new MainFrame(connection, record, transaction, report);
        } catch (SQLException e) {
            System.err.println("An error occurred while interacting with the database: " + e.getMessage());
        }
    }
}
