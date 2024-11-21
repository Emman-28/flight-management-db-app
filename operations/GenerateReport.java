package operations;

import java.sql.*;

public class GenerateReport {
    private Connection connection;

    public GenerateReport(Connection connection) {
        this.connection = connection;
    }

    // TODO:
        public String passengerAirportTraffic(String originAirportCode, String startDate, String endDate) {
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
    }

    public String passengerDestinationTraffic(String destinationAirportCode, String startDate, String endDate) {
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
    }

    public String passengerCompanyTraffic(String companyName, String startDate, String endDate) {
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
            """;
    }
    
    // TODO:
    public void companyRevenue() throws SQLException {

    }

    // TODO:
    public void flightPerformance() throws SQLException {

    }
}
