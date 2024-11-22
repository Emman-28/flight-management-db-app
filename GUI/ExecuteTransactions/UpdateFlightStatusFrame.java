package GUI.ExecuteTransactions;

import operations.ExecuteTransaction;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UpdateFlightStatusFrame extends JFrame {

    private final ExecuteTransaction transaction;

    public UpdateFlightStatusFrame(Connection connection, ExecuteTransaction transaction) {
        this.transaction = transaction;

        setTitle("Update Flight Status");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        // instructions
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Update Flight Status:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        instructionPanel.add(instructionLabel);

        // dropdowns and text fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        JLabel flightIdLabel = new JLabel("Select Flight ID:");
        JComboBox<String> flightIdDropdown = new JComboBox<>(getFlightIds(connection));
        inputPanel.add(flightIdLabel);
        inputPanel.add(flightIdDropdown);

        JLabel currentStatusLabel = new JLabel("Current Flight Status:");
        JTextField currentStatusField = new JTextField();
        currentStatusField.setEditable(false);
        inputPanel.add(currentStatusLabel);
        inputPanel.add(currentStatusField);

        JLabel newStatusLabel = new JLabel("Select New Status:");
        String[] statuses = {"Scheduled", "On Air", "Arrived", "Delayed", "Cancelled"};
        JComboBox<String> newStatusDropdown = new JComboBox<>(statuses);
        inputPanel.add(newStatusLabel);
        inputPanel.add(newStatusDropdown);

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton updateButton = new JButton("Update Status");
        JButton backButton = new JButton("Back");

        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        // event listeners
        flightIdDropdown.addActionListener(e -> {
            String selectedFlightId = (String) flightIdDropdown.getSelectedItem();
            if (selectedFlightId != null) {
                currentStatusField.setText(getCurrentStatus(connection, selectedFlightId));
            }
        });

        updateButton.addActionListener(e -> {
            String selectedFlightId = (String) flightIdDropdown.getSelectedItem();
            String newStatus = (String) newStatusDropdown.getSelectedItem();
            if (selectedFlightId != null && newStatus != null) {
                try {
                    transaction.updateFlightStatus(selectedFlightId, newStatus);
                    JOptionPane.showMessageDialog(this, "Flight status updated successfully!");
                    currentStatusField.setText(getCurrentStatus(connection, selectedFlightId));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating flight status: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select both a flight ID and a new status.");
            }
        });

        backButton.addActionListener(e -> dispose());

        // Add panels to main frame
        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // flight ids to integrate with dropdown
    private String[] getFlightIds(Connection connection) {
        ArrayList<String> flightIds = new ArrayList<>();
        String query = "SELECT flight_id FROM flights";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                flightIds.add(rs.getString("flight_id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching flight IDs: " + e.getMessage());
        }

        return flightIds.toArray(new String[0]);
    }

    // fetch current status of selected flight
    private String getCurrentStatus(Connection connection, String flightId) {
        String query = "SELECT flight_status FROM flights WHERE flight_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, flightId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("flight_status");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching flight status: " + e.getMessage());
        }
        return "Unknown";
    }
}
