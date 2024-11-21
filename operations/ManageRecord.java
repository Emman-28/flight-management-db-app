package operations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageRecord {
    private Connection connection;

    public ManageRecord(Connection connection) {
        this.connection = connection;
    }

    // Function to create a record in the specified columns of a given table
    public void create(String table, String[] columns, Object[] values) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");

        // Appending columns to query
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }

        // Add placeholders for the values
        sql.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            sql.append("?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        // Execute the query using a PreparedStatement
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            // Binding values to placeholders
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }

            // Executing the insert query
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            System.out.println("Error occurred while inserting record: " + e.getMessage());
            throw e;
        }
    }

    // Method to read all records from a table
    public String readAll(String table) throws SQLException {
        String query = "SELECT * FROM " + table;
        StringBuilder result = new StringBuilder();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(resultSet.getString(i)).append(" ");
                }
                result.append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while reading the record: " + e.getMessage());
            throw e;
        }

        return result.toString();
    }

    // Method to read records with a condition
    public String readWithCondition(String table, String condition) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + condition;
        StringBuilder result = new StringBuilder();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(resultSet.getString(i)).append(" ");
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

    // Method to read using a custom query and return List<Object[]>
    public List<Object[]> readWithQuery(String query) throws SQLException {
        List<Object[]> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while reading records: " + e.getMessage());
            throw e;
        }

        return results;
    }


    public void update(String table, String condition, String[] columns, Object[] values) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");

        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i] + " = ?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE " + condition);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
            System.out.println("Error occurred while updating record: " + e.getMessage());
            throw e;
        }
    }

    public void delete(String table, String condition) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE " + condition;

        try (Statement statement = connection.createStatement()) {
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

    public void delete(String table) throws SQLException {
        String sql = "DELETE FROM " + table;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}
