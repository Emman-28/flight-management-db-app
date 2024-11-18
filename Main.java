import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        Scanner input = new Scanner(System.in);
        int choice = 0;

        try {
            // establishing a connection #FIXME: can make it so that the user inputs the url, user, and password
            connection = DatabaseConnection.getConnection();
            System.out.println("Connected to the database successfully!");
            System.out.println("\nWelcome to the Flight Database Management System!\n");

            while(true) {
                // displaying options
                System.out.println("[1] Manage Records");
                System.out.println("[2] Execute Transactions");
                System.out.println("[3] Generate Reports");
                System.out.println("[4] Exit System");

                System.out.println("\nWhat would you like to do today?");
                System.out.print("> ");
                choice = input.nextInt(); // taking user input
                System.out.println("");

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
                                    // update record
                                    // delete record

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
                        DatabaseConnection.closeConnection(connection, statement, null);
                        System.out.println("\nClosed connection with database...");
                        System.out.println("\nExited the Flight Database Management System.");
                        return;
                    default:
                        System.out.println("Invalid option, please input a valid option.");
                }
            }

            /* 
            // Create a statement to execute SQL queries
            statement = connection.createStatement();
            // Select a specific row from the 'booking' table (e.g., booking_id = 101)
            String selectQuery = "SELECT * FROM booking WHERE booking_id = 101";
            resultSet = statement.executeQuery(selectQuery);
            // Check if the result set contains data
            if (resultSet.next()) {
            // Print the row data
            System.out.println("Booking Details:");
            System.out.println("Booking ID: " + resultSet.getInt("booking_id"));
            System.out.println("Passenger ID: " + resultSet.getInt("passenger_id"));
            System.out.println("Flight ID: " + resultSet.getString("flight_id"));
            System.out.println("Airport ID: " + resultSet.getInt("airport_id"));
            System.out.println("Booking Date: " +
            resultSet.getTimestamp("booking_date"));
            System.out.println("Booking Status: " +
            resultSet.getString("booking_status"));
            }
            */

        } catch (SQLException e) {
            System.err.println("An error occurred while interacting with the database: " + e.getMessage());
        } finally {
            // closing connection
            DatabaseConnection.closeConnection(connection, statement, null);
            System.out.println("Closed connection with database...");
        }
    }
}
