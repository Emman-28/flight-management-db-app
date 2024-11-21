package GUI;

import GUI.ManageRecords.*;

import javax.swing.*;
import java.awt.*;
import operations.ManageRecord;
import operations.ExecuteTransaction;
import operations.GenerateReport;

public class ManageRecordsFrame extends JFrame {

    public ManageRecordsFrame(ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        setTitle("Manage Records");
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
        JLabel instructionLabel = new JLabel("Select which record to manage:", SwingConstants.CENTER);
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
        JButton aircraftButton = new JButton("Aircraft Record Management");
        JButton airportButton = new JButton("Airport Record Management");
        JButton bookingButton = new JButton("Booking Record Management");
        JButton companyButton = new JButton("Company Record Management");
        JButton flightButton = new JButton("Flight Record Management");
        JButton flightLogButton = new JButton("Flight Log Record Management");
        JButton passengerButton = new JButton("Passenger Record Management");
        JButton passportButton = new JButton("Passport Record Management");
        JButton ticketButton = new JButton("Ticket Record Management");

        // Set uniform size for all buttons
        aircraftButton.setPreferredSize(buttonSize);
        airportButton.setPreferredSize(buttonSize);
        bookingButton.setPreferredSize(buttonSize);
        companyButton.setPreferredSize(buttonSize);
        flightButton.setPreferredSize(buttonSize);
        flightLogButton.setPreferredSize(buttonSize);
        passengerButton.setPreferredSize(buttonSize);
        passportButton.setPreferredSize(buttonSize);
        ticketButton.setPreferredSize(buttonSize);

        // Add action listeners to each button
        aircraftButton.addActionListener(e -> {
            dispose();
            new AircraftManagementFrame(manageRecord, transaction, report);
        });
        airportButton.addActionListener(e -> {
            dispose();
            new AirportManagementFrame(manageRecord, transaction, report);
        });
        bookingButton.addActionListener(e -> {
            dispose();
            new BookingManagementFrame(manageRecord, transaction, report);
        });
        companyButton.addActionListener(e -> {
            dispose();
            new CompanyManagementFrame(manageRecord, transaction, report);
        });
        flightButton.addActionListener(e -> {
            dispose();
            new FlightManagementFrame(manageRecord, transaction, report);
        });
        flightLogButton.addActionListener(e -> {
            dispose();
            new FlightLogManagementFrame(manageRecord, transaction, report);
        });
        passengerButton.addActionListener(e -> {
            dispose();
            new PassengerManagementFrame(manageRecord, transaction, report);
        });
        passportButton.addActionListener(e -> {
            dispose();
            new PassportManagementFrame(manageRecord, transaction, report);
        });
        ticketButton.addActionListener(e -> {
            dispose();
            new TicketManagementFrame(manageRecord, transaction, report);
        });

        // Add buttons to the panel
        buttonPanel.add(aircraftButton);
        buttonPanel.add(airportButton);
        buttonPanel.add(bookingButton);
        buttonPanel.add(companyButton);
        buttonPanel.add(flightButton);
        buttonPanel.add(flightLogButton);
        buttonPanel.add(passengerButton);
        buttonPanel.add(passportButton);
        buttonPanel.add(ticketButton);

        // Back button panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Reduced vertical gap
        bottomPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.addActionListener(e -> {
            dispose();
            new MainFrame(manageRecord, transaction, report); // Reopen MainFrame
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
