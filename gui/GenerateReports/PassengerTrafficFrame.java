package gui.GenerateReports;

import gui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import operations.*;

public class PassengerTrafficFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public PassengerTrafficFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        // Frame settings
        setTitle("Passenger Traffic Report Generator");
        setSize(500, 500);
        setLocationRelativeTo(null); // Center window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("logo.png").getImage());

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

        mainPanel.setOpaque(false); // Make the main panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Passenger Traffic Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setOpaque(false); // Make the input panel transparent

        JLabel reportTypeLabel = new JLabel("Select Report Type:");
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"By Origin Airport", "By Destination Airport", "By Company"});
        inputPanel.add(reportTypeLabel);
        inputPanel.add(reportTypeComboBox);

        JLabel paramLabel = new JLabel("Enter Parameter:");
        JTextField paramField = new JTextField(15);
        inputPanel.add(paramLabel);
        inputPanel.add(paramField);

        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        JTextField startDateField = new JTextField(10);
        inputPanel.add(startDateLabel);
        inputPanel.add(startDateField);

        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        JTextField endDateField = new JTextField(10);
        inputPanel.add(endDateLabel);
        inputPanel.add(endDateField);

        gbc.gridy = 1;
        mainPanel.add(inputPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false); // Make button panel transparent

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> {
            String reportType = (String) reportTypeComboBox.getSelectedItem();
            String parameter = paramField.getText().trim();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();

            if (parameter.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Object[][] reportData = switch (reportType) {
                    case "By Origin Airport" -> report.passengerAirportTraffic(parameter, startDate, endDate, connection);
                    case "By Destination Airport" -> report.passengerDestinationTraffic(parameter, startDate, endDate, connection);
                    case "By Company" -> report.passengerCompanyTraffic(parameter, startDate, endDate, connection);
                    default -> null;
                };

                if (reportData == null || reportData.length == 0) {
                    JOptionPane.showMessageDialog(this, "No data available for the selected criteria.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String[] columnNames = {"Passengers", "Flights", "Delayed Flights", "Cancelled Flights", "Successful Flights", "Total Payments"};
                JTable resultTable = new JTable(reportData, columnNames);
                resultTable.setFillsViewportHeight(true); // Make the table fill the viewport
                JScrollPane scrollPane = new JScrollPane(resultTable);

                JOptionPane.showMessageDialog(this, scrollPane, "Passenger Traffic Report", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching the report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new GenerateReportsFrame(connection, manageRecord, transaction, report);
        });

        buttonPanel.add(generateButton);
        buttonPanel.add(backButton);

        gbc.gridy = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }
}
