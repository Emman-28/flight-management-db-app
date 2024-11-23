package operations;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.*;
import java.math.*;

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

        // flightLogManager.addFlightLog(newLogId, flightId, logDate.toString(), eventTypeId);
        
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

    public void bookFlight(int passengerId, String flightId, String seatNumber, BigDecimal price) throws SQLException {
        connection.setAutoCommit(false); // Begin transaction

        try {
            // 1. Check flight availability
            if (!isFlightAvailable(flightId)) {
                throw new SQLException("No available flight found for the given criteria.");
            }

            // 2. Check flight status
            if (!isFlightBookable(flightId)) {
                throw new SQLException("Flight status is not available for booking.");
            }

            // 3. Check seat availability
            if (!hasAvailableSeats(flightId)) {
                throw new SQLException("No seats are available on the selected flight.");
            }

            // 4. Check and retrieve or create passport and passenger records
            int passportId = ensurePassportExists(passengerId);
            int finalPassengerId = ensurePassengerExists(passengerId, passportId);

            // 5. Create a booking record
            int bookingId = createBookingRecord(finalPassengerId, flightId);

            // 6. Create a ticket record
            createTicketRecord(bookingId, seatNumber, price);

            // 7. Update flight seating capacity
            updateFlightSeating(flightId);

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of failure
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restore default behavior
        }
    }

    private boolean isFlightAvailable(String flightId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM flights WHERE flight_id = ? AND expected_departure_time > NOW()";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, flightId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private boolean isFlightBookable(String flightId) throws SQLException {
        String sql = "SELECT flight_status FROM flights WHERE flight_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, flightId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("flight_status");
                return "Available".equalsIgnoreCase(status);
            }
            return false;
        }
    }

    private boolean hasAvailableSeats(String flightId) throws SQLException {
        String sql = "SELECT seating_capacity FROM flights WHERE flight_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, flightId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("seating_capacity") > 0;
            }
            return false;
        }
    }

    private int ensurePassportExists(int passengerId) throws SQLException {
        // Check if a passport exists for the given passenger
        String sql = "SELECT passport_id FROM passports WHERE passport_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, passengerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("passport_id");
            } else {
                throw new SQLException("Passport record not found. Please create a passport record first.");
            }
        }
    }

    private int ensurePassengerExists(int passengerId, int passportId) throws SQLException {
        // Check if a passenger exists, otherwise create one
        String selectSQL = "SELECT passenger_id FROM passengers WHERE passenger_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectSQL)) {
            selectStatement.setInt(1, passengerId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("passenger_id");
            } else {
                // Create a passenger record
                String insertSQL = "INSERT INTO passengers (passenger_id, passport_id) VALUES (?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                    insertStatement.setInt(1, passengerId);
                    insertStatement.setInt(2, passportId);
                    insertStatement.executeUpdate();
                    ResultSet keys = insertStatement.getGeneratedKeys();
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                    throw new SQLException("Failed to create passenger record.");
                }
            }
        }
    }

    private int createBookingRecord(int passengerId, String flightId) throws SQLException {
        String insertSQL = "INSERT INTO bookings (passenger_id, flight_id, booking_date, booking_status) VALUES (?, ?, NOW(), 'Paid')";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, passengerId);
            preparedStatement.setString(2, flightId);
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("Failed to create booking record.");
        }
    }

    private void createTicketRecord(int bookingId, String seatNumber, BigDecimal price) throws SQLException {
        String insertSQL = "INSERT INTO tickets (booking_id, seat_number, price) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, bookingId);
            preparedStatement.setString(2, seatNumber);
            preparedStatement.setBigDecimal(3, price);
            preparedStatement.executeUpdate();
        }
    }

    private void updateFlightSeating(String flightId) throws SQLException {
        String updateSQL = "UPDATE flights SET seating_capacity = seating_capacity - 1 WHERE flight_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, flightId);
            preparedStatement.executeUpdate();
        }
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
