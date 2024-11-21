package operations;

import java.sql.*;

public class ExecuteTransaction {
    private Connection connection;

    public ExecuteTransaction(Connection connection) {
        this.connection = connection;
    }

    public void updateFlightStatus(String flightId, String flightStatus) throws SQLException {
        String updateFlightSQL = "UPDATE flights SET flight_status = ?, actual_departure_time = ?, actual_arrival_time = ? WHERE flight_id = ?";
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        int eventTypeId;

        // set conditions based on flight status
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFlightSQL)) {
            if (flightStatus.equals("On Air")) {
                eventTypeId = 1; // Departure
                preparedStatement.setString(1, flightStatus);
                preparedStatement.setTimestamp(2, timestamp);
                preparedStatement.setNull(3, java.sql.Types.TIMESTAMP);
            } 
            else if (flightStatus.equals("Arrived")) {
                eventTypeId = 2; // Arrival
                preparedStatement.setString(1, flightStatus);
                preparedStatement.setNull(2, java.sql.Types.TIMESTAMP);
                preparedStatement.setTimestamp(3, timestamp);
            } 
            else {
                throw new IllegalArgumentException("Invalid flight status for update.");
            }

            // set flight ID and execute update
            preparedStatement.setString(4, flightId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // placeholder for success message
                // System.out.println("Flight status updated successfully.");
                
                // add log
                transFlightLog(flightId, eventTypeId);
            } 
            else {
                // placeholder for error message
                // System.out.println("Flight not found with given ID.");
            }
        }
    }

    // updateFlightSchedule: updates the schedule of a flight and logs the event
    public void transFlightLog(String flightId, int eventTypeId) throws SQLException {
        String getMaxLogIdSQL = "SELECT COALESCE(MAX(log_id), 0) + 1 AS new_log_id FROM flight_logs";
        int newLogId = 0;
        LocalDateTime now = LocalDateTime.now();
        Timestamp logDate = Timestamp.valueOf(now);

        // get new logID
        try (PreparedStatement preparedStatement = connection.prepareStatement(getMaxLogIdSQL)) {
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                newLogId = resultSet.getInt("new_log_id");
            }
        }

        flightLogManager.addFlightLog(newLogId, flightId, logDate.toString(), eventTypeId);
        
        // placeholder for success message
        // System.out.println("Flight log added with log ID: " + newLogId);

        String newBookingStatus = determineBookingStatus(eventTypeId);
        if (newBookingStatus != null) {
            updateBookingStatus(flightId, eventTypeId, newBookingStatus);
        }
    }

    // updates the booking status based on the event type
    public void updateBookingStatus(String flightId, int eventTypeId, String newBookingStatus) throws SQLException {
        String updateBookingSQL = "UPDATE bookings SET booking_status = ? WHERE flight_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookingSQL)) {
            preparedStatement.setString(1, newBookingStatus);
            preparedStatement.setString(2, flightId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // placeholder for success message
                // System.out.println("Booking status updated successfully to " + newBookingStatus + ".");
            } 
            else {
                // placeholder for error message
                // System.out.println("No bookings found for the given flight ID.");
            }
        }
    }

    // switch case to determine booking status based on event type
    private String determineBookingStatus(int eventTypeId) {
        return switch (eventTypeId) {
            case 2 -> "Completed"; // Arrival
            case 4 -> "Pending"; // Cancellation
            case 5, 8 -> "Rescheduled"; // Change in Departure Time or Rescheduling
            case 6, 7, 10 -> "Pending"; // Technical Maintenance, Weather Disruption, or Medical Emergency
            default -> null; // No status update for other event types
        };
    }

    // TODO:
    public void bookFlight() throws SQLException {

    }

    // TODO:
    public void refundBooking() throws SQLException {

    }

    // TODO:
    public void rescheduleBooking() throws SQLException {

    }

    // TODO:
    public void updateFlight() throws SQLException {

    }
}
