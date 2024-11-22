package gui.ExecuteTransactions;

import gui.ExecuteTransactionsFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
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

    // Method to load available flights for rescheduling
    private void loadAvailableFlights() {
        String query = "SELECT flight_id, aircraft_id FROM flights WHERE seating_capacity > 0";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                flightComboBox.addItem(rs.getString("flight_id") + " - " + rs.getString("aircraft_id"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading flights: " + e.getMessage());
        }
    }

    // Method to process rescheduling of the booking
    private void processReschedule() {
        String selectedBookingId = (String) bookingComboBox.getSelectedItem();
        String selectedFlight = (String) flightComboBox.getSelectedItem();
        if (selectedBookingId == null || selectedFlight == null) {
            statusLabel.setText("Please select both a booking and a flight.");
            return;
        }

        String[] flightDetails = selectedFlight.split(" - ");
        String newFlightId = flightDetails[0];

        String fetchDetailsQuery = "SELECT flight_id FROM bookings WHERE booking_id = ?";
        String updateBookingQuery = "UPDATE bookings SET flight_id = ? WHERE booking_id = ?";
        String updateOldFlightQuery = "UPDATE flights SET seating_capacity = seating_capacity + 1 WHERE flight_id = ?";
        String updateNewFlightQuery = "UPDATE flights SET seating_capacity = seating_capacity - 1 WHERE flight_id = ?";

        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchDetailsQuery);
             PreparedStatement updateBookingStmt = connection.prepareStatement(updateBookingQuery);
             PreparedStatement updateOldFlightStmt = connection.prepareStatement(updateOldFlightQuery);
             PreparedStatement updateNewFlightStmt = connection.prepareStatement(updateNewFlightQuery)) {

            // Start transaction
            connection.setAutoCommit(false);

            // Fetch old flight_id for the booking
            fetchStmt.setString(1, selectedBookingId);
            ResultSet rs = fetchStmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Booking ID not found.");
            }
            String oldFlightId = rs.getString("flight_id");

            // Update booking to the new flight
            updateBookingStmt.setString(1, newFlightId);
            updateBookingStmt.setString(2, selectedBookingId);
            updateBookingStmt.executeUpdate();

            // Update old flight seating capacity
            updateOldFlightStmt.setString(1, oldFlightId);
            updateOldFlightStmt.executeUpdate();

            // Update new flight seating capacity
            updateNewFlightStmt.setString(1, newFlightId);
            updateNewFlightStmt.executeUpdate();

            // Commit transaction
            connection.commit();
            statusLabel.setText("Booking rescheduled successfully.");
        } catch (SQLException e) {
            try {
                connection.rollback();
                statusLabel.setText("Reschedule failed: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                statusLabel.setText("Reschedule failed and rollback error: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                statusLabel.setText("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
}
