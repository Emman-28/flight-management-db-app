package operations;

import java.sql.*;

public class GenerateReport {
    private Connection connection;

    public GenerateReport(Connection connection) {
        this.connection = connection;
    }

    public String passengerAirportTraffic(String originAirportCode, String startDate, String endDate) {
        /*
        return """
            SELECT 
                COUNT(DISTINCT bookings.passenger_id) AS number_of_passengers,
                COUNT(DISTINCT flights.flight_id) AS number_of_flights,
                SUM(CASE WHEN flights.flight_status = 'Delayed' THEN 1 ELSE 0 END) AS flights_with_delay,
                SUM(CASE WHEN flights.flight_status = 'Cancelled' THEN 1 ELSE 0 END) AS flights_with_cancellation,
                SUM(CASE WHEN flights.flight_status = 'Arrived' THEN 1 ELSE 0 END) AS successful_flights,
                SUM(tickets.price) AS total_payments
            FROM flights
            JOIN bookings ON flights.flight_id = bookings.flight_id
            JOIN tickets ON bookings.booking_id = tickets.booking_id
            WHERE flights.origin_airport_id = (SELECT airport_id FROM airports WHERE name = '""" + originAirportCode + """')
              AND bookings.booking_date BETWEEN '""" + startDate + """' AND '""" + endDate + """'
              AND bookings.booking_status != 'Refunded'
              AND flights.flight_status != 'Cancelled'
            """;
        */

        return "";
    }

    public String passengerDestinationTraffic(String destinationAirportCode, String startDate, String endDate) {
        /*
        return """
            SELECT 
                COUNT(DISTINCT bookings.passenger_id) AS number_of_passengers,
                COUNT(DISTINCT flights.flight_id) AS number_of_flights,
                SUM(CASE WHEN flights.flight_status = 'Delayed' THEN 1 ELSE 0 END) AS flights_with_delay,
                SUM(CASE WHEN flights.flight_status = 'Cancelled' THEN 1 ELSE 0 END) AS flights_with_cancellation,
                SUM(CASE WHEN flights.flight_status = 'Arrived' THEN 1 ELSE 0 END) AS successful_flights,
                SUM(tickets.price) AS total_payments
            FROM flights
            JOIN bookings ON flights.flight_id = bookings.flight_id
            JOIN tickets ON bookings.booking_id = tickets.booking_id
            WHERE flights.dest_airport_id = (SELECT airport_id FROM airports WHERE name = '""" + destinationAirportCode + """')
              AND bookings.booking_date BETWEEN '""" + startDate + """' AND '""" + endDate + """'
              AND bookings.booking_status != 'Refunded'
              AND flights.flight_status != 'Cancelled'
            """;
            */
            return "";
    }

    public String passengerCompanyTraffic(String companyName, String startDate, String endDate) {
        /*
        return """
            SELECT 
                COUNT(DISTINCT bookings.passenger_id) AS number_of_passengers,
                COUNT(DISTINCT flights.flight_id) AS number_of_flights,
                SUM(CASE WHEN flights.flight_status = 'Delayed' THEN 1 ELSE 0 END) AS flights_with_delay,
                SUM(CASE WHEN flights.flight_status = 'Cancelled' THEN 1 ELSE 0 END) AS flights_with_cancellation,
                SUM(CASE WHEN flights.flight_status = 'Arrived' THEN 1 ELSE 0 END) AS successful_flights,
                SUM(tickets.price) AS total_payments
            FROM flights
            JOIN bookings ON flights.flight_id = bookings.flight_id
            JOIN tickets ON bookings.booking_id = tickets.booking_id
            WHERE flights.aircraft_id IN (
                SELECT aircraft_id FROM aircrafts 
                WHERE aircraft_id IN (SELECT aircraft_id FROM flights WHERE origin_airport_id IN (SELECT airport_id FROM airports WHERE company_id IN (SELECT company_id FROM companies WHERE name = '""" + companyName + """'))))
              AND bookings.booking_date BETWEEN '""" + startDate + """' AND '""" + endDate + """'
              AND bookings.booking_status != 'Refunded'
              AND flights.flight_status != 'Cancelled'
            """;*/

            return "";
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
