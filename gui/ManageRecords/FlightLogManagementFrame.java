package gui.ManageRecords;

import gui.ManageRecordsFrame;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import javax.imageio.ImageIO;
import javax.swing.*;
import operations.ExecuteTransaction;
import operations.FlightManager;
import operations.GenerateReport;
import operations.ManageRecord;

public class FlightManagementFrame extends JFrame {

    private final FlightManager flightManager;

    public FlightManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        setTitle("Flight Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("logo.png").getImage());

        this.flightManager = new FlightManager(connection);

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)) {
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

        mainPanel.setOpaque(false); // main panel background is transparent

        // instructions
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setOpaque(false); // transpaerent

        JLabel instructionLabel = new JLabel("Select a Flight Operation:");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        instructionPanel.add(instructionLabel);
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // vuttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        Dimension buttonSize = new Dimension(200, 40);

        // add flight button
        JButton addFlightButton = new JButton("Add Flight");
        addFlightButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addFlightButton.setMaximumSize(buttonSize);
        addFlightButton.setFont(new Font("Arial", Font.BOLD, 16));
        addFlightButton.setBackground(new Color(34, 139, 34)); // green
        addFlightButton.setForeground(Color.WHITE);
        addFlightButton.setFocusPainted(false);
        addFlightButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addFlightButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addFlightButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // update Flight Button
        JButton updateFlightButton = new JButton("Update Flight");
        updateFlightButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateFlightButton.setMaximumSize(buttonSize);
        updateFlightButton.setFont(new Font("Arial", Font.BOLD, 16));
        updateFlightButton.setBackground(new Color(70, 130, 180)); // blue
        updateFlightButton.setForeground(Color.WHITE);
        updateFlightButton.setFocusPainted(false);
        updateFlightButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateFlightButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(updateFlightButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // delete Flight Button
        JButton deleteFlightButton = new JButton("Delete Flight");
        deleteFlightButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteFlightButton.setMaximumSize(buttonSize);
        deleteFlightButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteFlightButton.setBackground(new Color(178, 34, 34)); // reddish
        deleteFlightButton.setForeground(Color.WHITE);
        deleteFlightButton.setFocusPainted(false);
        deleteFlightButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteFlightButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(deleteFlightButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // view Flights Button
        JButton viewAllFlightsButton = new JButton("View All Flights");
        viewAllFlightsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewAllFlightsButton.setMaximumSize(buttonSize);
        viewAllFlightsButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewAllFlightsButton.setBackground(new Color(255, 140, 0)); // oprange
        viewAllFlightsButton.setForeground(Color.WHITE);
        viewAllFlightsButton.setFocusPainted(false);
        viewAllFlightsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllFlightsButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(viewAllFlightsButton);

        // button panel to instruction panel
        instructionPanel.add(buttonPanel);

        // back
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        backPanel.setOpaque(false); // Transparent
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(105, 105, 105)); // Dim Gray
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        backPanel.add(backButton);

        // add panels to main panel
        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(backPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // action listeners for Buttons
        addFlightButton.addActionListener(e -> addFlight());
        updateFlightButton.addActionListener(e -> updateFlight());
        deleteFlightButton.addActionListener(e -> deleteFlight());
        viewAllFlightsButton.addActionListener(e -> viewAllFlights());

        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report);
        });

        setVisible(true);
    }

    private void addFlight() {
        try {
            String flightId = JOptionPane.showInputDialog("Enter Flight ID:");
            if (flightId == null || flightId.trim().isEmpty()) throw new Exception("Flight ID cannot be empty.");

            String expectedDepartureTime = JOptionPane.showInputDialog("Enter Expected Departure Time (YYYY-MM-DD HH:MM:SS):");
            if (expectedDepartureTime == null || expectedDepartureTime.trim().isEmpty()) throw new Exception("Expected Departure Time cannot be empty.");

            String expectedArrivalTime = JOptionPane.showInputDialog("Enter Expected Arrival Time (YYYY-MM-DD HH:MM:SS):");
            if (expectedArrivalTime == null || expectedArrivalTime.trim().isEmpty()) throw new Exception("Expected Arrival Time cannot be empty.");

            String actualDepartureTime = JOptionPane.showInputDialog("Enter Actual Departure Time (YYYY-MM-DD HH:MM:SS) or leave blank:");
            if (actualDepartureTime != null && actualDepartureTime.trim().isEmpty()) {
                actualDepartureTime = null; // Treat empty string as null
            }

            String actualArrivalTime = JOptionPane.showInputDialog("Enter Actual Arrival Time (YYYY-MM-DD HH:MM:SS) or leave blank:");
            if (actualArrivalTime != null && actualArrivalTime.trim().isEmpty()) {
                actualArrivalTime = null; // Treat empty string as null
            }

            String aircraftId = JOptionPane.showInputDialog("Enter Aircraft ID:");
            if (aircraftId == null || aircraftId.trim().isEmpty()) throw new Exception("Aircraft ID cannot be empty.");

            String originAirportIdStr = JOptionPane.showInputDialog("Enter Origin Airport ID:");
            if (originAirportIdStr == null || originAirportIdStr.trim().isEmpty()) throw new Exception("Origin Airport ID cannot be empty.");
            int originAirportId = Integer.parseInt(originAirportIdStr);

            String destAirportIdStr = JOptionPane.showInputDialog("Enter Destination Airport ID:");
            if (destAirportIdStr == null || destAirportIdStr.trim().isEmpty()) throw new Exception("Destination Airport ID cannot be empty.");
            int destAirportId = Integer.parseInt(destAirportIdStr);

            String flightStatus = JOptionPane.showInputDialog("Enter Flight Status (Scheduled, Delayed, etc.):");
            if (flightStatus == null || flightStatus.trim().isEmpty()) throw new Exception("Flight Status cannot be empty.");

            String seatingCapacityStr = JOptionPane.showInputDialog("Enter Seating Capacity:");
            if (seatingCapacityStr == null || seatingCapacityStr.trim().isEmpty()) throw new Exception("Seating Capacity cannot be empty.");
            int seatingCapacity = Integer.parseInt(seatingCapacityStr);

            flightManager.addFlight(flightId, expectedDepartureTime, expectedArrivalTime,
                    actualDepartureTime, actualArrivalTime, aircraftId,
                    originAirportId, destAirportId, flightStatus, seatingCapacity);
            JOptionPane.showMessageDialog(this, "Flight added successfully!");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + nfe.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // update flight Prompt
    private void updateFlight() {
        try {
            String flightId = JOptionPane.showInputDialog("Enter Flight ID to Update:");
            if (flightId == null || flightId.trim().isEmpty()) throw new Exception("Flight ID cannot be empty.");

            String[] updateOptions = {"Update Departure Time", "Update Arrival Time", "Update Status"};
            int updateChoice = JOptionPane.showOptionDialog(this,
                    "What would you like to update?",
                    "Update Flight",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    updateOptions,
                    updateOptions[0]);

            if (updateChoice == JOptionPane.CLOSED_OPTION) {
                return; // User closed the dialog
            }

            if (updateChoice == 0) {
                String newDepartureTime = JOptionPane.showInputDialog("Enter New Departure Time (YYYY-MM-DD HH:MM:SS):");
                if (newDepartureTime == null || newDepartureTime.trim().isEmpty()) throw new Exception("Departure Time cannot be empty.");
                flightManager.updateFlight(flightId, new String[]{"expected_departure_time"}, new Object[]{newDepartureTime});
                JOptionPane.showMessageDialog(this, "Departure time updated successfully!");
            } else if (updateChoice == 1) {
                String newArrivalTime = JOptionPane.showInputDialog("Enter New Arrival Time (YYYY-MM-DD HH:MM:SS):");
                if (newArrivalTime == null || newArrivalTime.trim().isEmpty()) throw new Exception("Arrival Time cannot be empty.");
                flightManager.updateFlight(flightId, new String[]{"expected_arrival_time"}, new Object[]{newArrivalTime});
                JOptionPane.showMessageDialog(this, "Arrival time updated successfully!");
            } else if (updateChoice == 2) {
                String newStatus = JOptionPane.showInputDialog("Enter New Flight Status:");
                if (newStatus == null || newStatus.trim().isEmpty()) throw new Exception("Flight Status cannot be empty.");
                flightManager.updateFlight(flightId, new String[]{"flight_status"}, new Object[]{newStatus});
                JOptionPane.showMessageDialog(this, "Flight status updated successfully!");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + nfe.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // delete Flight Prompt
    private void deleteFlight() {
        try {
            String flightId = JOptionPane.showInputDialog("Enter Flight ID to Delete:");
            if (flightId == null || flightId.trim().isEmpty()) throw new Exception("Flight ID cannot be empty.");
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Flight ID: " + flightId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                flightManager.deleteFlight(flightId);
                JOptionPane.showMessageDialog(this, "Flight deleted successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Deletion Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // view all flights Prompt
    private void viewAllFlights() {
        try {
            String result = flightManager.viewAllFlights();
            JTextArea textArea = new JTextArea(result);
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 400));
            JOptionPane.showMessageDialog(this, scrollPane, "All Flights Records", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "View Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
