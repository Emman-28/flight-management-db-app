import java.sql.*;

public class Main {
    // Update the URL with the correct database name at the end
    private static final String URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12744795";
    private static final String USER = "sql12744795";
    private static final String PASSWORD = "VIL4kus9lZ";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Load the MySQL JDBC driver (optional in some setups)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");

            // Create a statement to execute SQL queries
            statement = connection.createStatement();

            // Insert a new record into the 'booking' table
            String insertQuery = "INSERT INTO booking (booking_id, passenger_id, flight_id, airport_id, booking_date, booking_status) "
                    + "VALUES (101, 1, 'FL123', 2, '2024-11-14 10:00:00', 'Paid')";
            statement.executeUpdate(insertQuery);
            System.out.println("Booking inserted successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}