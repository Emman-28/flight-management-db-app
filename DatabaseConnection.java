import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12744795";
    private static final String USER = "sql12744795";
    private static final String PASSWORD = "VIL4kus9lZ";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
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
