import java.sql.*;
import java.util.Scanner;
import operations.*;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        Scanner input = new Scanner(System.in);
        int choice = 0;
        String result;

        try {
            // establishing a connection #FIXME: can make it so that the user inputs the url, user, and password
            connection = DatabaseConnection.connect();
            System.out.println("Connected to the database successfully!");
            System.out.println("\nWelcome to the Flight Database Management System!\n");

            ManageRecord record = new ManageRecord(connection);
            ExecuteTransaction transaction = new ExecuteTransaction(connection);
            GenerateReport report = new GenerateReport(connection);

            while(true) {
                // displaying options
                System.out.println("You can command the following operations:");
                System.out.println("[1] Manage Records");
                System.out.println("[2] Execute Transactions");
                System.out.println("[3] Generate Reports");
                System.out.println("[4] Exit System");

                System.out.println("\nWhat would you like to do today?");
                System.out.print("> ");
                choice = input.nextInt(); // taking user input
                System.out.print(" ");

                switch (choice) {
                    case 1: // managing records
                        // choosing which core record table to manage
                            // TODO: airport table
                                // let user choose what to do
                                    // create record
                                    // read record
                                    // update record
                                    // delete record

                            // TODO: passenger and passport tables
                                // let user choose what to do
                                    // create record
                                    // read record
                                    System.out.println("Reading Passenger Table...\n");
                                    result = record.read("passenger");
                                    System.out.println(result);
                                    // update record
                                    // delete record
                                    record.delete("passenger", "passenger_id = 1");

                            // TODO: flight table
                            // let user choose what to do
                                    // create record
                                    // read record
                                    // update record
                                    // delete record
                        break;
                    case 2: // executing transactions
                        // choosing what transaction to execute 
                        // TODO: booking a flight
                            // see gdoc
                        // TODO: refunding a booking
                            // see gdoc
                        // TODO: rescheduling a booking
                            // see gdoc
                        // TODO: updating a flight
                            // see gdoc

                        break;  
                    case 3: // generating reports
                        // choosing a report to generate
                        // TODO: passenger traffic report
                            // see gdoc
                        // TODO: company revenue report
                            // see gdoc
                        // TODO: flight performance report
                            // see gdoc
                        break;
                    case 4:
                        // exiting program
                        DatabaseConnection.closeConnection();
                        System.out.println("\nClosed connection with database...");
                        System.out.println("\nExited the Flight Database Management System.");
                        return;
                    default:
                        System.out.println("Invalid option, please input a valid option.\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while interacting with the database: " + e.getMessage());
        } finally {
            // closing connection
            DatabaseConnection.closeConnection();
            System.out.print("\nClosed connection with database...");
        }
    }
}
