package GUI;

import GUI.ManageRecords.*;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import operations.*;

public class GenerateReportsFrame extends JFrame {

    public GenerateReportsFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        setTitle("Generate Reports");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)); // Reduced gaps
        mainPanel.setBackground(Color.WHITE);

        // Instruction panel with vertical spacing
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setBackground(Color.WHITE);

        // Add space above the selection message
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Adds space after the title

        // Instruction label
        JLabel instructionLabel = new JLabel("Select which report to generate:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(instructionLabel);

        // Add space after the instruction message
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Reduced vertical spacing
        buttonPanel.setBackground(Color.WHITE);

        // Create buttons with uniform size
        Dimension buttonSize = new Dimension(250, 40);
        JButton airportButton = new JButton("Airport Record Management");
        JButton flightButton = new JButton("Flight Record Management");
        JButton passportButton = new JButton("Passport Record Management");
        JButton companyButton = new JButton("Company Record Management");  // New button for Company Record Management
        JButton aircraftButton = new JButton("Aircraft Record Management");  // New button for Aircraft Record Management

        // Set uniform size for all buttons
        airportButton.setPreferredSize(buttonSize);
        flightButton.setPreferredSize(buttonSize);
        passportButton.setPreferredSize(buttonSize);
        companyButton.setPreferredSize(buttonSize);  // Set size for new button
        aircraftButton.setPreferredSize(buttonSize); // Set size for new button

        // Add action listeners to each button
        airportButton.addActionListener(e -> {
            dispose();
            new AirportManagementFrame(connection, manageRecord, transaction, report);
        });
        flightButton.addActionListener(e -> {
            dispose();
            new FlightManagementFrame(connection, manageRecord, transaction, report);
        });
        passportButton.addActionListener(e -> {
            dispose();
            new PassportManagementFrame(connection, manageRecord, transaction, report);
        });
        companyButton.addActionListener(e -> {
            dispose();
            new CompanyManagementFrame(connection, manageRecord, transaction, report); // New Company Management Frame
        });
        aircraftButton.addActionListener(e -> {
            dispose();
            new AircraftManagementFrame(connection, manageRecord, transaction, report); // New Aircraft Management Frame
        });

        // Add buttons to the panel
        buttonPanel.add(airportButton);
        buttonPanel.add(flightButton);
        buttonPanel.add(passportButton);
        buttonPanel.add(companyButton);  // Add new button to panel
        buttonPanel.add(aircraftButton); // Add new button to panel

        // Back button panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Reduced vertical gap
        bottomPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.addActionListener(e -> {
            dispose();
            new MainFrame(connection, manageRecord, transaction, report); // Reopen MainFrame
        });
        bottomPanel.add(backButton);

        // Add components to the main panel
        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Set up the frame
        add(mainPanel);
        setVisible(true);
    }
}
