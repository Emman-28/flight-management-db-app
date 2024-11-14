import java.sql.*;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/dbflights";
    private static final String USER = "root";
    private static final String PASSWORD = "Iwillrulzsql28!";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");

            // Create a statement to execute SQL queries
            statement = connection.createStatement();
            String query = "SELECT * FROM flights"; // Replace with your actual table name
            resultSet = statement.executeQuery(query);

            // Process the result set
            while (resultSet.next()) {
                // Use actual column names from your 'flights' table
                System.out.println("Flight ID: " + resultSet.getString("flight_id"));
            }
        } catch (SQLException e) {
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
