package operations;

import java.sql.Connection;
import java.sql.SQLException;

public class FlightManager {

    // uses managerecord class to perform crud operations on the database
    private ManageRecord manageRecord;

    // constructor to initialize managerecords
    public FlightManager(Connection connection) {
        this.manageRecord = new ManageRecord(connection);
    }

    // METHODS TO PERFORM CRUD OPERATIONS ON THE FLIGHTS TABLE
    // flightId, expectedDepartureTime, expectedArrivalTime, actualDepartureTime, 
    // actualArrivalTime, aircraftId, originAirportId, destAirportId, flightStatus, 
    // seatingCapacity

    // addFlight: adds new flight record
    public void addFlight(String flightId, String expectedDepartureTime, String expectedArrivalTime, String actualDepartureTime, String actualArrivalTime, String aircraftId, 
                          int originAirportId, int destAirportId, String flightStatus, int seatingCapacity) throws SQLException {
        String table = "flights";
        
        String[] columns = {"flight_id", "expected_departure_time", "expected_arrival_time", 
                            "actual_departure_time", "actual_arrival_time", "aircraft_id", 
                            "origin_airport_id", "dest_airport_id", "flight_status", "seating_capacity"};

        Object[] values = {flightId, expectedDepartureTime, expectedArrivalTime, actualDepartureTime, 
            actualArrivalTime, aircraftId, originAirportId, destAirportId, flightStatus, seatingCapacity};

        manageRecord.create(table, columns, values);
    }

    // updateFlight: updates flight record
    public void updateFlight(String flightId, String[] columns, Object[] values) throws SQLException {
        String table = "flights";

        String condition = "flight_id = '" + flightId + "'";

        manageRecord.update(table, condition, columns, values);
    }

    // deleteFlight: deletes flight record
    public void deleteFlight(String flightId) throws SQLException {
        String table = "flights";

        String condition = "flight_id = '" + flightId + "'";

        manageRecord.delete(table, condition);
    }

    // veiwAllFlights: view all flight records
    public String viewAllFlights() throws SQLException {
        String table = "flights";

        return manageRecord.readAll(table);
    }

    // viewFlightsWithCondition: view flight records with a condition
    public String viewFlightsWithCondition(String flightId) throws SQLException {
        String table = "flights";
        String condition = "flight_id = '" + flightId + "'";

        return manageRecord.readWithCondition(table, condition);
    }
}
