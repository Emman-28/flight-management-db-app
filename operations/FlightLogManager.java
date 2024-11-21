package operations;

import java.sql.Connection;
import java.sql.SQLException;

public class FlightLogManager {

    // uses ManageRecord class to perform CRUD operations on the database
    private ManageRecord manageRecord;

    // constructor to initialize ManageRecord
    public FlightLogManager(Connection connection) {
        this.manageRecord = new ManageRecord(connection);
    }

    // METHODS TO PERFORM CRUD OPERATIONS ON THE FLIGHT_LOGS TABLE
    // logId, flightId, logDate, eventTypeId

    // addFlightLog: adds a new flight log record
    public void addFlightLog(int logId, String flightId, String logDate, int eventTypeId) throws SQLException {
        String table = "flight_logs";

        String[] columns = {"log_id", "flight_id", "log_date", "event_type_id"};
        Object[] values = {logId, flightId, logDate, eventTypeId};

        manageRecord.create(table, columns, values);
    }

    // updateFlightLog: updates flight log record
    public void updateFlightLog(int logId, String[] columns, Object[] values) throws SQLException {
        String table = "flight_logs";

        String condition = "log_id = " + logId;

        manageRecord.update(table, condition, columns, values);
    }

    // deleteFlightLog: deletes flight log record
    public void deleteFlightLog(int logId) throws SQLException {
        String table = "flight_logs";

        String condition = "log_id = " + logId;

        manageRecord.delete(table, condition);
    }

    // viewAllFlightLogs: view all flight log records
    public String viewAllFlightLogs() throws SQLException {
        String table = "flight_logs";

        return manageRecord.readAll(table);
    }

    // viewFlightLogsWithCondition: view flight log records with a log id
    public String viewFlightLogsWithCondition(String logId) throws SQLException {
        String table = "flight_logs";
        String condition = "log_id = " + logId;

        return manageRecord.readWithCondition(table, condition);
    }
}
