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

    // returns all records from a given table FIXME: returns a string, not suitable for GUI
    public String read(String table) throws SQLException {
        String query = "SELECT * FROM " + table; // setting up query statement
        StringBuilder result = new StringBuilder(); // will house result of query; string builders are more efficient with larger strings

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
        } catch (SQLException e) {
            System.out.println("Error occurred while reading the record: " + e.getMessage());
            throw e;
        }
        return result.toString();
    }

    // #TODO:
    // returns all records from a given table and condition
    public String read(String table, String condition) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + condition; // setting up query statement
        StringBuilder result = new StringBuilder(); // will house result of query; string builders are more efficient with larger strings

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

    // deletes records from a given table and condition
    public void delete(String table, String condition) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE " + condition;
    
        try (Statement statement = connection.createStatement()) { // deleting records
            int rowsAffected = statement.executeUpdate(sql);
            if (rowsAffected > 0) {
                System.out.println("Successfully deleted " + rowsAffected + " record(s).");
            } else {
                System.out.println("No records found matching the condition.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while deleting record: " + e.getMessage());
            throw e;
        }
    }

    // deletes all records from a given table
    public void delete(String table) throws SQLException {
        String sql = "DELETE FROM " + table;
    
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql); // executing delete query
        }
    }
}