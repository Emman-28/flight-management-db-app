import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a connection using the DatabaseConnection class
            connection = DatabaseConnection.getConnection();
            System.out.println("Connected to the database!");

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
                System.out.println("Booking Date: " + resultSet.getTimestamp("booking_date"));
                System.out.println("Booking Status: " + resultSet.getString("booking_status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources using the DatabaseConnection class
            DatabaseConnection.closeConnection(connection, statement, null);
        }
    }
}
