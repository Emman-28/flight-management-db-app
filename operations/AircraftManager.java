package operations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AircraftManager {

    // uses managerecord class to perform crud operations on the database
    private ManageRecord manageRecord;

    // constructor to initialize managerecord
    public AircraftManager(Connection connection) {
        this.manageRecord = new ManageRecord(connection);
    }

    // METHODS TO PERFORM CRUD OPERATIONS ON THE AIRCRAFT TABLE
    // aircraftId: unique identifier for the aircraft
    // aircraftModel: model of the aircraft
    // maxCapacity: maximum number of passengers the aircraft can carry

    // addAircraft: adds new aircraft
    public void addAircraft(String aircraftId, String aircraftModel, int maxCapacity) throws SQLException {
        String table = "aircraft";

        String[] columns = {"aircraft_id", "aircraft_model", "max_capacity"};
        Object[] values = {aircraftId, aircraftModel, maxCapacity};

        manageRecord.create(table, columns, values);
    }

    // updateAircraft: updates an aircraft
    public void updateAircraft(String aircraftId, String aircraftModel, int maxCapacity) throws SQLException {
        String table = "aircraft";
        String condition = "aircraft_id = '" + aircraftId + "'";

        String[] columns = {"aircraft_model", "max_capacity"};
        Object[] values = {aircraftModel, maxCapacity};

        manageRecord.update(table, condition, columns, values);
    }

    // deleteAircraft: deletes an aircraft
    public void deleteAircraft(String aircraftId) throws SQLException {
        String table = "aircraft";
        String condition = "aircraft_id = '" + aircraftId + "'";

        manageRecord.delete(table, condition);
    }

    // getAllAircraft: retrieves all aircraft records
    public String getAllAircraft() throws SQLException {
        String table = "aircraft";

        return manageRecord.readAll(table);
    }

    // getAircraftById: retrieves an aircraft record by aircraftId
    public String getAircraftById(String aircraftId) throws SQLException {
        String table = "aircraft";
        String condition = "aircraft_id = '" + aircraftId + "'";
        
        return manageRecord.readWithCondition(table, condition);
    }
}
