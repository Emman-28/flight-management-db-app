package operations;

import java.sql.*;

public class GenerateReport {
    private Connection connection;

    public GenerateReport(Connection connection) {
        this.connection = connection;
    }

    public Object[][] passengerAirportTraffic(int originAirportId, String startDate, String endDate) throws SQLException {
        String query = """
            SELECT 
                COUNT(DISTINCT b.passenger_id) AS number_of_passengers,
                COUNT(DISTINCT f.flight_id) AS number_of_flights,
                SUM(CASE WHEN f.flight_status = 'Delayed' THEN 1 ELSE 0 END) AS flights_with_delay,
                SUM(CASE WHEN f.flight_status = 'Cancelled' THEN 1 ELSE 0 END) AS flights_with_cancellation,
                SUM(CASE WHEN f.flight_status = 'Arrived' THEN 1 ELSE 0 END) AS successful_flights,
                SUM(t.price) AS total_payments
            FROM flights f
            JOIN bookings b ON f.flight_id = b.flight_id
            JOIN tickets t ON b.booking_id = t.booking_id
            WHERE f.origin_airport_id = ?
              AND b.booking_date BETWEEN ? AND ?
              AND b.booking_status != 'Refunded'
              AND f.flight_status != 'Cancelled';
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setInt(1, originAirportId);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);
            ResultSet rs = stmt.executeQuery();

            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][6]; // 6 columns
            int rowIndex = 0;

            while (rs.next()) {
                data[rowIndex][0] = rs.getInt("number_of_passengers");
                data[rowIndex][1] = rs.getInt("number_of_flights");
                data[rowIndex][2] = rs.getInt("flights_with_delay");
                data[rowIndex][3] = rs.getInt("flights_with_cancellation");
                data[rowIndex][4] = rs.getInt("successful_flights");
                data[rowIndex][5] = rs.getDouble("total_payments");
                rowIndex++;
            }
            return data;
        }
    }

    public Object[][] passengerDestinationTraffic(int destinationAirportId, String startDate, String endDate) throws SQLException {
        String query = """
            SELECT 
                COUNT(DISTINCT b.passenger_id) AS number_of_passengers,
                COUNT(DISTINCT f.flight_id) AS number_of_flights,
                SUM(CASE WHEN f.flight_status = 'Delayed' THEN 1 ELSE 0 END) AS flights_with_delay,
                SUM(CASE WHEN f.flight_status = 'Cancelled' THEN 1 ELSE 0 END) AS flights_with_cancellation,
                SUM(CASE WHEN f.flight_status = 'Arrived' THEN 1 ELSE 0 END) AS successful_flights,
                SUM(t.price) AS total_payments
            FROM flights f
            JOIN bookings b ON f.flight_id = b.flight_id
            JOIN tickets t ON b.booking_id = t.booking_id
            WHERE f.dest_airport_id = ?
              AND b.booking_date BETWEEN ? AND ?
              AND b.booking_status != 'Refunded'
              AND f.flight_status != 'Cancelled';
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setInt(1, destinationAirportId);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);
            ResultSet rs = stmt.executeQuery();

            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][6]; // 6 columns
            int rowIndex = 0;

            while (rs.next()) {
                data[rowIndex][0] = rs.getInt("number_of_passengers");
                data[rowIndex][1] = rs.getInt("number_of_flights");
                data[rowIndex][2] = rs.getInt("flights_with_delay");
                data[rowIndex][3] = rs.getInt("flights_with_cancellation");
                data[rowIndex][4] = rs.getInt("successful_flights");
                data[rowIndex][5] = rs.getDouble("total_payments");
                rowIndex++;
            }
            return data;
        }
    }

    

    public Object[][] passengerCompanyTraffic(String companyName, String startDate, String endDate, Connection connection) throws SQLException {
        String query = """
            SELECT 
                COUNT(DISTINCT b.passenger_id) AS number_of_passengers,
                COUNT(DISTINCT f.flight_id) AS number_of_flights,
                SUM(CASE WHEN f.flight_status = 'Delayed' THEN 1 ELSE 0 END) AS flights_with_delay,
                SUM(CASE WHEN f.flight_status = 'Cancelled' THEN 1 ELSE 0 END) AS flights_with_cancellation,
                SUM(CASE WHEN f.flight_status = 'Arrived' THEN 1 ELSE 0 END) AS successful_flights,
                SUM(t.price) AS total_payments
            FROM flights f
            JOIN bookings b ON f.flight_id = b.flight_id
            JOIN tickets t ON b.booking_id = t.booking_id
            WHERE f.aircraft_id IN (
                SELECT aircraft_id FROM aircrafts 
                WHERE aircraft_id IN (
                    SELECT aircraft_id FROM flights 
                    WHERE origin_airport_id IN (
                        SELECT airport_id FROM airports 
                        WHERE company_id IN (
                            SELECT company_id FROM companies WHERE name = ?
                        )
                    )
                )
            )
              AND b.booking_date BETWEEN ? AND ?
              AND b.booking_status != 'Refunded'
              AND f.flight_status != 'Cancelled';
        """;
    
        try (PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setString(1, companyName);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);
            ResultSet rs = stmt.executeQuery();
    
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
    
            Object[][] data = new Object[rowCount][6]; // 6 columns
            int rowIndex = 0;
    
            while (rs.next()) {
                data[rowIndex][0] = rs.getInt("number_of_passengers");
                data[rowIndex][1] = rs.getInt("number_of_flights");
                data[rowIndex][2] = rs.getInt("flights_with_delay");
                data[rowIndex][3] = rs.getInt("flights_with_cancellation");
                data[rowIndex][4] = rs.getInt("successful_flights");
                data[rowIndex][5] = rs.getDouble("total_payments");
                rowIndex++;
            }
            return data;
        }
    }   

    public Object[][] companyRevenue(int selectedYear, Connection connection) throws SQLException {
        String query = """
            SELECT 
                YEAR(b.booking_date) AS year,
                c.name AS company_name,
                SUM(t.price) AS total_revenue
            FROM 
                companies c
            JOIN 
                airports a ON c.company_id = a.company_id
            JOIN 
                flights f ON f.origin_airport_id = a.airport_id
            JOIN 
                bookings b ON b.flight_id = f.flight_id
            JOIN 
                tickets t ON t.booking_id = b.booking_id
            WHERE 
                YEAR(b.booking_date) = ? 
            GROUP BY 
                c.name, YEAR(b.booking_date)
            ORDER BY 
                total_revenue DESC;
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setInt(1, selectedYear);  // Set selected year
            ResultSet rs = stmt.executeQuery();
            
            // Count rows for the result set
            rs.last();  // Now allowed because the result set is scrollable
            int rowCount = rs.getRow();
            rs.beforeFirst();  // Reset to the first row
            
            // Prepare data array with rows and columns (only 3 columns: Year, Company Name, Revenue)
            Object[][] data = new Object[rowCount][3]; 
            
            int rowIndex = 0;
            while (rs.next()) {
                data[rowIndex][0] = rs.getString("year");  // Year as String
                data[rowIndex][1] = rs.getString("company_name");  // Company Name as String
                data[rowIndex][2] = rs.getDouble("total_revenue");  // Total Revenue as Double
                rowIndex++;
            }
            
            return data;
        }
    }
    
    // TODO:
    public void flightPerformance() throws SQLException {

    }
}
