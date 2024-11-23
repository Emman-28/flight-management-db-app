package gui.ExecuteTransactions;

import gui.ExecuteTransactionsFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import operations.*;

public class RescheduleBookingFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;
    private JComboBox<String> bookingComboBox;
    private JComboBox<String> flightComboBox;
    private JLabel statusLabel;

    // Constructor for initializing the frame and components
    public RescheduleBookingFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        // Frame settings
        setTitle("Flight Booking Rescheduling");
        setSize(500, 500);
        setLocationRelativeTo(null); // Centers window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("logo.png").getImage());

        // Main panel with background image
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = ImageIO.read(new File("db bg.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        mainPanel.setOpaque(false);

        // Components
        JLabel instructionLabel = new JLabel("Rescheduling a Booking:");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 40)); // Title
        JLabel promptLabel = new JLabel("Select a Booking ID to Reschedule");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Instruction text
        bookingComboBox = new JComboBox<>(); // Dropdown for booking IDs
        JLabel flightLabel = new JLabel("Select a New Flight:");
        flightLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Flight label
        flightComboBox = new JComboBox<>(); // Dropdown for available flights
        JButton rescheduleButton = new JButton("Reschedule Booking");
        rescheduleButton.setPreferredSize(new Dimension(250, 40)); // Reschedule button
        rescheduleButton.setMaximumSize(new Dimension(250, 40)); 
        statusLabel = new JLabel(" "); // To show success/error messages

        // Create and set up the Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(75, 30));

        // Populate bookingComboBox with refundable bookings (from the database)
        loadBookingIDs();
        loadAvailableFlights();

        // Add ActionListener to rescheduleButton
        rescheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processReschedule(); // Process reschedule when button is clicked
            }
        });

        // Layout components in the window
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(instructionLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(promptLabel, gbc);

        gbc.gridy = 2;
        mainPanel.add(bookingComboBox, gbc);

        gbc.gridy = 3;
        mainPanel.add(flightLabel, gbc);

        gbc.gridy = 4;
        mainPanel.add(flightComboBox, gbc);

        gbc.gridy = 5;
        mainPanel.add(rescheduleButton, gbc);

        gbc.gridy = 6;
        mainPanel.add(cancelButton, gbc);

        gbc.gridy = 7;
        mainPanel.add(statusLabel, gbc);

        // Cancel button to go back to the previous screen
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close current window
                new ExecuteTransactionsFrame(connection, manageRecord, transaction, report); // Open previous screen
            }
        });

        // Add this inside your constructor, after initializing the bookingComboBox
        bookingComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAvailableFlights();  // Refresh flights based on selected booking
            }
        });

        // Add the main panel to the frame and set it visible
        add(mainPanel);
        setVisible(true);
    }

    // Method to load all booking IDs that are reschedulable
    private void loadBookingIDs() {
        String query = "SELECT booking_id FROM bookings WHERE booking_status IN ('Paid', 'Pending', 'Rescheduled')";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                bookingComboBox.addItem(rs.getString("booking_id"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading bookings: " + e.getMessage());
        }
    }

    private void loadAvailableFlights() {
        String selectedBookingId = (String) bookingComboBox.getSelectedItem();
        if (selectedBookingId == null) {
            statusLabel.setText("Please select a booking.");
            return;
        }

        // Query to fetch origin and destination country names for the selected booking
        String getCountriesQuery = "SELECT a1.country_name AS origin_country, a2.country_name AS dest_country "
                                  + "FROM bookings b "
                                  + "JOIN airports a1 ON b.airport_id = a1.airport_id "
                                  + "JOIN flights f ON b.flight_id = f.flight_id "
                                  + "JOIN airports a2 ON f.dest_airport_id = a2.airport_id "
                                  + "WHERE b.booking_id = ?";

        try (PreparedStatement getCountriesStmt = connection.prepareStatement(getCountriesQuery)) {
            getCountriesStmt.setString(1, selectedBookingId);
            try (ResultSet rs = getCountriesStmt.executeQuery()) {
                if (rs.next()) {
                    String originCountry = rs.getString("origin_country");
                    String destCountry = rs.getString("dest_country");

                    // Query to fetch available flights where origin and destination countries match
                    String flightsQuery = "SELECT f.flight_id, f.aircraft_id "
                                        + "FROM flights f "
                                        + "JOIN airports a1 ON f.origin_airport_id = a1.airport_id "
                                        + "JOIN airports a2 ON f.dest_airport_id = a2.airport_id "
                                        + "WHERE a1.country_name = ? AND a2.country_name = ? "
                                        + "AND f.seating_capacity > 0";

                    try (PreparedStatement flightsStmt = connection.prepareStatement(flightsQuery)) {
                        flightsStmt.setString(1, originCountry);  // Set origin country
                        flightsStmt.setString(2, destCountry);   // Set destination country

                        try (ResultSet flightRs = flightsStmt.executeQuery()) {
                            // Clear existing items in flightComboBox
                            flightComboBox.removeAllItems();

                            // Add matching flights to the combo box
                            while (flightRs.next()) {
                                String flightDetails = flightRs.getString("flight_id") + " - " + flightRs.getString("aircraft_id");
                                flightComboBox.addItem(flightDetails);
                            }
                        } catch (SQLException e) {
                            statusLabel.setText("Error loading flights: " + e.getMessage());
                        }
                    }

                } else {
                    statusLabel.setText("Booking details not found.");
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading booking details: " + e.getMessage());
        }
    }

    private void processReschedule() {
        String selectedBookingId = (String) bookingComboBox.getSelectedItem();
        String selectedFlight = (String) flightComboBox.getSelectedItem();
        if (selectedBookingId == null || selectedFlight == null) {
            statusLabel.setText("Please select both a booking and a flight.");
            return;
        }
    
        String[] flightDetails = selectedFlight.split(" - ");
        String newFlightId = flightDetails[0];
    
        // Queries for fetching and updating booking and tickets
        String fetchDetailsQuery = "SELECT flight_id, passenger_id FROM bookings WHERE booking_id = ?";
        String deleteOldTicketQuery = "DELETE FROM tickets WHERE booking_id = ?";
        String insertNewTicketQuery = "INSERT INTO tickets (passenger_id, booking_id, seat_number, price) VALUES (?, ?, ?, ?)";
        String updateBookingQuery = "UPDATE bookings SET flight_id = ? WHERE booking_id = ?";
        String updateOldFlightQuery = "UPDATE flights SET seating_capacity = seating_capacity + 1 WHERE flight_id = ?";
        String updateNewFlightQuery = "UPDATE flights SET seating_capacity = seating_capacity - 1 WHERE flight_id = ?";
    
        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchDetailsQuery);
             PreparedStatement deleteOldTicketStmt = connection.prepareStatement(deleteOldTicketQuery);
             PreparedStatement insertNewTicketStmt = connection.prepareStatement(insertNewTicketQuery);
             PreparedStatement updateBookingStmt = connection.prepareStatement(updateBookingQuery);
             PreparedStatement updateOldFlightStmt = connection.prepareStatement(updateOldFlightQuery);
             PreparedStatement updateNewFlightStmt = connection.prepareStatement(updateNewFlightQuery)) {
    
            connection.setAutoCommit(false); // Start transaction
    
            // Fetch current flight and passenger details
            fetchStmt.setString(1, selectedBookingId);
            ResultSet rs = fetchStmt.executeQuery();
            if (rs.next()) {
                String oldFlightId = rs.getString("flight_id");
                String passengerId = rs.getString("passenger_id");
    
                // Query to get the price of the old ticket
                String getTicketPriceQuery = "SELECT price FROM tickets WHERE booking_id = ? AND passenger_id = ?";
                double price = 0;
                try (PreparedStatement priceStmt = connection.prepareStatement(getTicketPriceQuery)) {
                    priceStmt.setString(1, selectedBookingId);
                    priceStmt.setString(2, passengerId);
    
                    ResultSet priceRs = priceStmt.executeQuery();
                    if (priceRs.next()) {
                        price = priceRs.getDouble("price");
                    } else {
                        statusLabel.setText("No ticket found for the given booking and passenger.");
                        connection.rollback();
                        return;
                    }
                }
    
                // Delete old ticket
                deleteOldTicketStmt.setString(1, selectedBookingId);
                deleteOldTicketStmt.executeUpdate();
    
                // Insert new ticket with dynamically assigned seat number
                String seatNumber = getAvailableSeat(); // Get available seat for the new flight
                if (seatNumber == null) {
                    statusLabel.setText("No available seats on the new flight.");
                    connection.rollback(); // Rollback transaction on failure
                    return;
                }
    
                insertNewTicketStmt.setString(1, passengerId);  // Passenger ID
                insertNewTicketStmt.setString(2, selectedBookingId); // Booking ID
                insertNewTicketStmt.setString(3, seatNumber); // Seat number
                insertNewTicketStmt.setDouble(4, price); // Price
                int rowsInserted = insertNewTicketStmt.executeUpdate();
                if (rowsInserted == 0) {
                    statusLabel.setText("Error inserting new ticket.");
                    connection.rollback();
                    return;
                }
    
                // Update booking with new flight
                updateBookingStmt.setString(1, newFlightId);
                updateBookingStmt.setString(2, selectedBookingId);
                updateBookingStmt.executeUpdate();
    
                // Update flight capacities
                updateOldFlightStmt.setString(1, oldFlightId);
                updateOldFlightStmt.executeUpdate();
                updateNewFlightStmt.setString(1, newFlightId);
                updateNewFlightStmt.executeUpdate();
    
                connection.commit(); // Commit transaction
                statusLabel.setText("Booking rescheduled successfully.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error rescheduling booking: " + e.getMessage());
            try {
                connection.rollback(); // Rollback on failure
            } catch (SQLException rollbackEx) {
                statusLabel.setText("Rollback error: " + rollbackEx.getMessage());
            }
        }
    }
    
    // Helper method to get the next available seat number for the new flight
    public String getAvailableSeat() {
        Random random = new Random();
        
        // Define the number of rows and seats per row (e.g., 30 rows and 6 seats per row)
        int rows = 30;
        char[] seats = {'A', 'B', 'C', 'D', 'E', 'F'};

        // Randomly pick a row number between 1 and the maximum number of rows
        int row = random.nextInt(rows) + 1;

        // Randomly pick a seat letter from the available seat options
        char seat = seats[random.nextInt(seats.length)];

        // Combine row and seat letter to form the full seat identifier (e.g., 5A, 12C)
        return row + "" + seat;
    }
}
