package GUI.ManageRecords;

import operations.FlightManager;
import operations.ManageRecord;
import operations.ExecuteTransaction;
import operations.GenerateReport;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class FlightManagementFrame extends JFrame {

    private final FlightManager flightManager;

    public FlightManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        setTitle("Flight Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.flightManager = new FlightManager(connection);

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.WHITE);

        // prompt panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Select a Flight Operation:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        instructionPanel.add(instructionLabel);

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setBackground(Color.WHITE);
        Dimension buttonSize = new Dimension(200, 40);

        JButton addFlightButton = new JButton("Add Flight");
        JButton updateFlightButton = new JButton("Update Flight");
        JButton deleteFlightButton = new JButton("Delete Flight");
        JButton viewAllFlightsButton = new JButton("View All Flights");

        addFlightButton.setPreferredSize(buttonSize);
        updateFlightButton.setPreferredSize(buttonSize);
        deleteFlightButton.setPreferredSize(buttonSize);
        viewAllFlightsButton.setPreferredSize(buttonSize);

        // adds action listeners to buttons
        addFlightButton.addActionListener(e -> addFlight());
        updateFlightButton.addActionListener(e -> updateFlight());
        deleteFlightButton.addActionListener(e -> deleteFlight());
        viewAllFlightsButton.addActionListener(e -> viewAllFlights());

        buttonPanel.add(addFlightButton);
        buttonPanel.add(updateFlightButton);
        buttonPanel.add(deleteFlightButton);
        buttonPanel.add(viewAllFlightsButton);

        // back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        bottomPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report); // Back to ManageRecordsFrame
        });
        bottomPanel.add(backButton);

        // components
        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // addprompt
    private void addFlight() {
        try {
            String flightId = JOptionPane.showInputDialog("Enter Flight ID:");
            String expectedDepartureTime = JOptionPane.showInputDialog("Enter Expected Departure Time (YYYY-MM-DD HH:MM:SS):");
            String expectedArrivalTime = JOptionPane.showInputDialog("Enter Expected Arrival Time (YYYY-MM-DD HH:MM:SS):");
            String actualDepartureTime = JOptionPane.showInputDialog("Enter Actual Departure Time (YYYY-MM-DD HH:MM:SS) or leave blank:");
            String actualArrivalTime = JOptionPane.showInputDialog("Enter Actual Arrival Time (YYYY-MM-DD HH:MM:SS) or leave blank:");
            String aircraftId = JOptionPane.showInputDialog("Enter Aircraft ID:");
            int originAirportId = Integer.parseInt(JOptionPane.showInputDialog("Enter Origin Airport ID:"));
            int destAirportId = Integer.parseInt(JOptionPane.showInputDialog("Enter Destination Airport ID:"));
            String flightStatus = JOptionPane.showInputDialog("Enter Flight Status (Scheduled, Delayed, etc.):");
            int seatingCapacity = Integer.parseInt(JOptionPane.showInputDialog("Enter Seating Capacity:"));

            flightManager.addFlight(flightId, expectedDepartureTime, expectedArrivalTime,
                    actualDepartureTime, actualArrivalTime, aircraftId,
                    originAirportId, destAirportId, flightStatus, seatingCapacity);
            JOptionPane.showMessageDialog(this, "Flight added successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // update prompt
    private void updateFlight() {
        try {
            String flightId = JOptionPane.showInputDialog("Enter Flight ID to Update:");
            String[] updateOptions = {"Update Departure Time", "Update Arrival Time", "Update Status"};
            int updateChoice = JOptionPane.showOptionDialog(this,
                    "What would you like to update?",
                    "Update Flight",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    updateOptions,
                    updateOptions[0]);

            if (updateChoice == 0) {
                String newDepartureTime = JOptionPane.showInputDialog("Enter New Departure Time (YYYY-MM-DD HH:MM:SS):");
                flightManager.updateFlight(flightId, new String[]{"expected_departure_time"}, new Object[]{newDepartureTime});
                JOptionPane.showMessageDialog(this, "Departure time updated successfully!");
            } else if (updateChoice == 1) {
                String newArrivalTime = JOptionPane.showInputDialog("Enter New Arrival Time (YYYY-MM-DD HH:MM:SS):");
                flightManager.updateFlight(flightId, new String[]{"expected_arrival_time"}, new Object[]{newArrivalTime});
                JOptionPane.showMessageDialog(this, "Arrival time updated successfully!");
            } else if (updateChoice == 2) {
                String newStatus = JOptionPane.showInputDialog("Enter New Flight Status:");
                flightManager.updateFlight(flightId, new String[]{"flight_status"}, new Object[]{newStatus});
                JOptionPane.showMessageDialog(this, "Flight status updated successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // delete prompt
    private void deleteFlight() {
        try {
            String flightId = JOptionPane.showInputDialog("Enter Flight ID to Delete:");
            flightManager.deleteFlight(flightId);
            JOptionPane.showMessageDialog(this, "Flight deleted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // view all prompt
    private void viewAllFlights() {
        try {
            String result = flightManager.viewAllFlights();
            JTextArea textArea = new JTextArea(result);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 400));
            JOptionPane.showMessageDialog(this, scrollPane, "All Flights Records", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
