package operations;

import java.sql.*;

public class ManageRecord {
    private Connection connection;

    public ManageRecord(Connection connection) {
        this.connection = connection;
    }

    // TODO:
    public void create(String table, String[] columns, Object[] values) throws SQLException {

    }

    // FIXME: returns a string, not suitable for GUI
    public String read(String table) throws SQLException {
        String query = "SELECT * FROM " + table; // setting up query statement
        StringBuilder result = new StringBuilder(); // will house result of query; stringbuilders are more efficient with larger strings

        try(Statement statement = connection.createStatement(); 
        ResultSet resultSet = statement.executeQuery(query)) { // executes query and assigns result

            // retrieving number of columns present
            ResultSetMetaData metaData = resultSet.getMetaData(); 
            int columnCount = metaData.getColumnCount();

            // looping through rows of result
            while(resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) { // iterating over every column
                    result.append(resultSet.getString(i)).append(" "); // appending to result
                }
                result.append("\n"); // to indicate next row
            }
        }
        return result.toString();
    }

    // TODO:
    public void update(String tableName, String condition, String[] columns, Object[] values) throws SQLException {

    }

    // TODO:
    public void deleteRecord(String tableName, String condition) throws SQLException {

    }
}