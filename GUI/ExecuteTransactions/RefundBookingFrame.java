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

public class RefundBookingFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;
    private JComboBox<String> bookingComboBox;
    private JLabel statusLabel;

    public RefundBookingFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        // Frame settings
        setTitle("Flight Booking Refunding");
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
        JLabel instructionLabel = new JLabel("Refunding a Booking:");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 40));
        JLabel promptLabel = new JLabel("Select a Booking ID to Refund");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bookingComboBox = new JComboBox<>();
        JButton refundButton = new JButton("Refund Booking");
        refundButton.setPreferredSize(new Dimension(250, 40));
        refundButton.setMaximumSize(new Dimension(250, 40));
        statusLabel = new JLabel(" ");

        // Create buttons
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(75, 30));

        // Populate bookingComboBox with refundable bookings
        loadRefundableBookings();

        // Add ActionListener to refundButton
        refundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRefund();
            }
        });

        // Layout components
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
        mainPanel.add(refundButton, gbc);

        gbc.gridy = 4;
        mainPanel.add(cancelButton, gbc);

        gbc.gridy = 5;
        mainPanel.add(statusLabel, gbc);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ExecuteTransactionsFrame(connection, manageRecord, transaction, report);
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private void loadRefundableBookings() {
        String query = "SELECT booking_id FROM bookings WHERE booking_status IN ('Paid', 'Pending', 'Rescheduled')";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                bookingComboBox.addItem(rs.getString("booking_id"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading bookings: " + e.getMessage());
        }
    }

    private void processRefund() {
        String selectedBookingId = (String) bookingComboBox.getSelectedItem();
        if (selectedBookingId == null) {
            statusLabel.setText("No booking selected.");
            return;
        }

        String fetchDetailsQuery = "SELECT flight_id FROM bookings WHERE booking_id = ?";
        String updateBookingQuery = "UPDATE bookings SET booking_status = 'Refunded' WHERE booking_id = ?";
        String deleteTicketQuery = "DELETE FROM tickets WHERE booking_id = ?";
        String updateCapacityQuery = "UPDATE flights SET seating_capacity = seating_capacity + 1 WHERE flight_id = ?";

        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchDetailsQuery);
             PreparedStatement updateBookingStmt = connection.prepareStatement(updateBookingQuery);
             PreparedStatement deleteTicketStmt = connection.prepareStatement(deleteTicketQuery);
             PreparedStatement updateCapacityStmt = connection.prepareStatement(updateCapacityQuery)) {

            // Start transaction
            connection.setAutoCommit(false);

            // Fetch flight_id for the booking
            fetchStmt.setString(1, selectedBookingId);
            ResultSet rs = fetchStmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Booking ID not found.");
            }
            String flightId = rs.getString("flight_id");

            // Update booking status
            updateBookingStmt.setString(1, selectedBookingId);
            updateBookingStmt.executeUpdate();

            // Delete associated ticket
            deleteTicketStmt.setString(1, selectedBookingId);
            deleteTicketStmt.executeUpdate();

            // Update flight seating capacity
            updateCapacityStmt.setString(1, flightId);
            updateCapacityStmt.executeUpdate();

            // Commit transaction
            connection.commit();
            statusLabel.setText("Booking refunded successfully.");
            bookingComboBox.removeItem(selectedBookingId);
        } catch (SQLException e) {
            try {
                connection.rollback();
                statusLabel.setText("Refund failed: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                statusLabel.setText("Refund failed and rollback error: " + rollbackEx.getMessage());
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
