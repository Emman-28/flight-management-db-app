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

    // TODO:
    public void read(String table) throws SQLException {

    }

    // TODO:
    public void update(String tableName, String condition, String[] columns, Object[] values) throws SQLException {

    }

    // TODO:
    public void deleteRecord(String tableName, String condition) throws SQLException {

    }
}