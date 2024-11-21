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

            ManageRecord record = new ManageRecord(connection);
            ExecuteTransaction transaction = new ExecuteTransaction(connection);
            GenerateReport report = new GenerateReport(connection);

            // Pass the connection and managers to the GUI
            new MainFrame(connection, record, transaction, report);
        } catch (SQLException e) {
            System.err.println("An error occurred while interacting with the database: " + e.getMessage());
        }
    }
}