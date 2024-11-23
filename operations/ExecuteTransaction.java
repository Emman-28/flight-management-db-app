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
        int eventTypeId = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateFlightSQL)) {
            switch (flightStatus.toLowerCase()) {
                case "on air":
                    eventTypeId = 1; // Departure
                    preparedStatement.setString(1, flightStatus);
                    preparedStatement.setTimestamp(2, timestamp); // Set actual_departure_time
                    preparedStatement.setNull(3, Types.TIMESTAMP); // Clear actual_arrival_time
                    break;
                case "arrived":
                    eventTypeId = 2; // Arrival
                    preparedStatement.setString(1, flightStatus);
                    preparedStatement.setNull(2, Types.TIMESTAMP); // Clear actual_departure_time
                    preparedStatement.setTimestamp(3, timestamp); // Set actual_arrival_time
                    break;
                case "scheduled":
                case "delayed":
                case "cancelled":
                    eventTypeId = determineEventTypeId(flightStatus);
                    preparedStatement.setString(1, flightStatus);
                    preparedStatement.setNull(2, Types.TIMESTAMP); // No departure time update
                    preparedStatement.setNull(3, Types.TIMESTAMP); // No arrival time update
                    break;
                default:
                    throw new IllegalArgumentException("Invalid flight status for update.");
            }

            // Set flight ID and execute update
            preparedStatement.setString(4, flightId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Log the flight event
                transFlightLog(flightId, eventTypeId);
            } else {
                throw new SQLException("Flight not found with ID: " + flightId);
            }
        }
    }

    public void transFlightLog(String flightId, int eventTypeId) throws SQLException {
        String getMaxLogIdSQL = "SELECT COALESCE(MAX(log_id), 0) + 1 AS new_log_id FROM flight_logs";
        int newLogId = 0;
        LocalDateTime now = LocalDateTime.now();
        Timestamp logDate = Timestamp.valueOf(now);

        // Get new log ID
        try (PreparedStatement preparedStatement = connection.prepareStatement(getMaxLogIdSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                newLogId = resultSet.getInt("new_log_id");
            }
        }

        // insert
        String insertLogSQL = "INSERT INTO flight_logs (log_id, flight_id, log_date, event_type_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertLogSQL)) {
            preparedStatement.setInt(1, newLogId);
            preparedStatement.setString(2, flightId);
            preparedStatement.setTimestamp(3, logDate);
            preparedStatement.setInt(4, eventTypeId);
            preparedStatement.executeUpdate();
        }

        // determiunes the new booking status based on the event type
        String newBookingStatus = determineBookingStatus(eventTypeId);
        if (newBookingStatus != null) {
            updateBookingStatus(flightId, eventTypeId, newBookingStatus);
        }
    }

    public void updateBookingStatus(String flightId, int eventTypeId, String newBookingStatus) throws SQLException {
        String updateBookingSQL = "UPDATE bookings SET booking_status = ? WHERE flight_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookingSQL)) {
            preparedStatement.setString(1, newBookingStatus);
            preparedStatement.setString(2, flightId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No bookings found for the given flight ID: " + flightId);
            }
        }
    }

    private int determineEventTypeId(String flightStatus) {
        return switch (flightStatus.toLowerCase()) {
            case "scheduled" -> 3;   // Example ID for Scheduled
            case "delayed" -> 4;     // Example ID for Delayed
            case "cancelled" -> 5;   // Example ID for Cancelled
            default -> 0;             // No event type
        };
    }

    private String determineBookingStatus(int eventTypeId) {
        return switch (eventTypeId) {
            case 2 -> "Completed";          // Arrival
            case 4 -> "Pending";            // Cancellation
            case 5, 8 -> "Rescheduled";     // Change in Departure Time or Rescheduling
            case 6, 7, 10 -> "Pending";     // Technical Maintenance, Weather Disruption, or Medical Emergency
            default -> null;                // No status update for other event types
        };
    }

    public void bookFlight(int passengerId, String flightId, String seatNumber, BigDecimal price) throws SQLException {
        connection.setAutoCommit(false);  // Start a transaction

        try {
            // 1. Ensure the seat number is valid (optional validation)
            if (seatNumber == null || seatNumber.isEmpty()) {
                throw new IllegalArgumentException("Seat number cannot be null or empty.");
            }

            // 2. Create a booking record and get the booking ID
            int bookingId = createBookingRecord(passengerId, flightId);

            // 3. Log the booking ID to ensure it was created
            System.out.println("Booking ID created: " + bookingId);

            // 4. Create a ticket record with the booking ID and seat number
            createTicketRecord(passengerId, bookingId, seatNumber, price);

            // 5. Update the flight seating capacity (if needed)
            updateFlightSeating(flightId);

            connection.commit();  // Commit the transaction

            // No need to return anything, as it's now a void method
            System.out.println("Flight booking successful!");

        } catch (SQLException e) {
            connection.rollback();  // Rollback in case of an error
            System.err.println("Error during flight booking: " + e.getMessage());
            throw new SQLException("Booking failed: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);  // Restore auto-commit mode
        }
    }


    private int createBookingRecord(int passengerId, String flightId) throws SQLException {
        // Step 1: Verify that passenger exists
        String checkPassengerSql = "SELECT COUNT(*) FROM passengers WHERE passenger_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkPassengerSql)) {
            checkPs.setInt(1, passengerId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next() || rs.getInt(1) == 0) {
                    throw new SQLException("Passenger ID does not exist.");
                }
            }
        }

// Step 2: Verify that flight exists
        String checkFlightSql = "SELECT COUNT(*) FROM flights WHERE flight_id = ?";
        try (PreparedStatement checkFlightPs = connection.prepareStatement(checkFlightSql)) {
            // Trim flight_id to remove any leading or trailing spaces
            flightId = flightId.trim();
            System.out.println("Checking existence of flight with ID: " + flightId);  // Log the flight_id

            // Perform the query to check if the flight exists
            checkFlightPs.setString(1, flightId);

            try (ResultSet rs = checkFlightPs.executeQuery()) {
                if (!rs.next() || rs.getInt(1) == 0) {
                    throw new SQLException("Flight ID does not exist.");
                }
            }
        }


        // Step 3: Generate a new booking ID
        String getMaxIdSql = "SELECT IFNULL(MAX(booking_id), 0) FROM bookings";
        int newBookingId;

        try (PreparedStatement getMaxIdStmt = connection.prepareStatement(getMaxIdSql);
             ResultSet rs = getMaxIdStmt.executeQuery()) {
            if (rs.next()) {
                newBookingId = rs.getInt(1) + 1;
            } else {
                throw new SQLException("Failed to retrieve maximum booking ID.");
            }
        }

        // Step 4: Insert booking record
        String insertSql = """
        INSERT INTO bookings 
            (booking_id, passenger_id, flight_id, booking_date, booking_status, airport_id) 
        SELECT 
            ?, 
            ?, 
            ?, 
            NOW(), 
            'PAID',
            origin_airport_id
        FROM flights 
        WHERE flight_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setInt(1, newBookingId);  // Use the new booking ID
            ps.setInt(2, passengerId);
            ps.setString(3, flightId);
            ps.setString(4, flightId);

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows inserted into bookings: " + rowsAffected);

            if (rowsAffected == 0) {
                throw new SQLException("No rows were inserted into the bookings table.");
            }
        }

        return newBookingId;  // Return the newly generated booking ID
    }




    private void createTicketRecord(int passengerId, int bookingId, String seatNumber, BigDecimal price) throws SQLException {
        // Step 1: Verify that booking exists (in case the booking was not created successfully)
        String checkBookingSql = "SELECT COUNT(*) FROM bookings WHERE booking_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkBookingSql)) {
            checkPs.setInt(1, bookingId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next() || rs.getInt(1) == 0) {
                    throw new SQLException("Booking ID does not exist.");
                }
            }
        }

        // Step 2: Insert ticket record with the validated booking_id
        String insertTicketSql = """
        INSERT INTO tickets (booking_id, passenger_id, seat_number, price)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(insertTicketSql)) {
            ps.setInt(1, bookingId);  // Use the booking_id that was created in createBookingRecord
            ps.setInt(2, passengerId);
            ps.setString(3, seatNumber);
            ps.setBigDecimal(4, price);

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows inserted into tickets: " + rowsAffected);

            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert ticket record.");
            }
        }
    }


    public boolean ensurePassportExists(int passengerId) throws SQLException {
        String sql = "SELECT passport_id FROM passports WHERE passport_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, passengerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();  // Returns true if passport exists, false otherwise
        }
    }

    public boolean doesPassengerExistWithPassport(int passportId) throws SQLException {
        String selectSQL = "SELECT passenger_id FROM passengers WHERE passport_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectSQL)) {
            selectStatement.setInt(1, passportId);
            ResultSet resultSet = selectStatement.executeQuery();
            return resultSet.next();
        }
    }

    private void updateFlightSeating(String flightId) throws SQLException {
        String updateSQL = "UPDATE flights SET seating_capacity = seating_capacity + 1 WHERE flight_id = ?";
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
