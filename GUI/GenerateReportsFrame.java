package GUI;

import GUI.GenerateReports.CompanyRevenueFrame;
import GUI.GenerateReports.FlightPerformanceFrame;
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
        JButton passengerButton = new JButton("Passenger Traffic");
        JButton companyButton = new JButton("Company Revenue");
        JButton flightButton = new JButton("Flight Performance");

        // Set uniform size for all buttons
        passengerButton.setPreferredSize(buttonSize);
        companyButton.setPreferredSize(buttonSize);
        flightButton.setPreferredSize(buttonSize);

        // Add action listeners to each button
        passengerButton.addActionListener(e -> {
            dispose();
            new PassportManagementFrame(connection, manageRecord, transaction, report);
        });
        companyButton.addActionListener(e -> {
            dispose();
            new CompanyRevenueFrame(connection, manageRecord, transaction, report);
        });
        flightButton.addActionListener(e -> {
            dispose();
            new FlightPerformanceFrame(connection, manageRecord, transaction, report);
        });

        // Add buttons to the panel
        buttonPanel.add(passengerButton);
        buttonPanel.add(companyButton);
        buttonPanel.add(flightButton);

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
