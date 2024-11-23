package gui.GenerateReports;

import gui.GenerateReportsFrame;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import operations.*;

public class PassengerTrafficFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    // GUI Components
    private JLabel parameterLabel;
    private JComboBox<AirportEntry> parameterComboBox; // Changed to hold AirportEntry objects
    private List<AirportEntry> airportList;

    // Custom class to represent an Airport with id and name
    private static class AirportEntry {
        private final int id;
        private final String name;

        public AirportEntry(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return id + " - " + name;
        }
    }

    public PassengerTrafficFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        // Frame setup
        setTitle("Passenger Traffic Report Generator");
        setSize(800, 600);
        setLocationRelativeTo(null); // Centers window
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

        mainPanel.setOpaque(false); // Make main panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("Passenger Traffic Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false); // Transparent
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.insets = new Insets(10, 10, 10, 10);
        gbcInput.fill = GridBagConstraints.HORIZONTAL;

        // Report selection
        JLabel reportTypeLabel = new JLabel("Select Report Type:");
        reportTypeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 0;
        gbcInput.gridy = 0;
        inputPanel.add(reportTypeLabel, gbcInput);

        String[] reportTypes = {"By Origin Airport", "By Destination Airport"}; // Removed "By Company"
        JComboBox<String> reportTypeComboBox = new JComboBox<>(reportTypes);
        reportTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 1;
        inputPanel.add(reportTypeComboBox, gbcInput);

        // Parameter input (Dropdown)
        parameterLabel = new JLabel("Select Airport:");
        parameterLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 0;
        gbcInput.gridy = 1;
        inputPanel.add(parameterLabel, gbcInput);

        parameterComboBox = new JComboBox<>();
        parameterComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        parameterComboBox.setPreferredSize(new Dimension(300, 25));
        gbcInput.gridx = 1;
        inputPanel.add(parameterComboBox, gbcInput);

        // Start date
        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startDateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 0;
        gbcInput.gridy = 2;
        inputPanel.add(startDateLabel, gbcInput);

        JTextField startDateField = new JTextField(10);
        startDateField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 1;
        inputPanel.add(startDateField, gbcInput);

        // End date
        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        endDateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 0;
        gbcInput.gridy = 3;
        inputPanel.add(endDateLabel, gbcInput);

        JTextField endDateField = new JTextField(10);
        endDateField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcInput.gridx = 1;
        inputPanel.add(endDateField, gbcInput);

        gbc.gridy = 1;
        mainPanel.add(inputPanel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false); // Make button panel transparent

        JButton generateButton = new JButton("Generate Report");
        generateButton.setFont(new Font("Arial", Font.BOLD, 16));
        generateButton.setPreferredSize(new Dimension(180, 40));
        buttonPanel.add(generateButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(backButton);

        // Add buttons to main panel
        gbc.gridy = 2;
        mainPanel.add(buttonPanel, gbc);

        // Output table
        JTable resultTable = new JTable();
        resultTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultTable.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        scrollPane.setVisible(false); // Hidden initially

        gbc.gridy = 3;
        mainPanel.add(scrollPane, gbc);
        add(mainPanel);

        // Initialize data lists
        initializeDataLists();

        // Populate parameterComboBox based on default report type
        updateParameterComboBox((String) reportTypeComboBox.getSelectedItem());

        // Add action listener to reportTypeComboBox to update parameterComboBox
        reportTypeComboBox.addActionListener(e -> {
            String selectedReport = (String) reportTypeComboBox.getSelectedItem();
            updateParameterComboBox(selectedReport);
        });

        // Action listener for generate button
        generateButton.addActionListener(e -> {
            String reportType = (String) reportTypeComboBox.getSelectedItem();
            Object selectedObject = parameterComboBox.getSelectedItem();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();

            // Validation
            if (selectedObject == null || startDate.isEmpty() || endDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidDate(startDate) || !isValidDate(endDate)) {
                JOptionPane.showMessageDialog(this, "Please enter dates in YYYY-MM-DD format.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ensure startDate is before or equal to endDate
            if (startDate.compareTo(endDate) > 0) {
                JOptionPane.showMessageDialog(this, "Start Date cannot be after End Date.", "Date Range Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Retrieve the selected AirportEntry object
            if (!(selectedObject instanceof AirportEntry)) {
                JOptionPane.showMessageDialog(this, "Invalid airport selection.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AirportEntry selectedAirport = (AirportEntry) selectedObject;
            int airportId = selectedAirport.getId();

            try {
                Object[][] reportData = null;
                switch (reportType) {
                    case "By Origin Airport":
                        reportData = report.passengerAirportTraffic(airportId, startDate, endDate);
                        break;
                    case "By Destination Airport":
                        reportData = report.passengerDestinationTraffic(airportId, startDate, endDate);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Invalid report type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }

                if (reportData == null || reportData.length == 0) {
                    JOptionPane.showMessageDialog(this, "No data available for the selected criteria.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                    scrollPane.setVisible(false);
                    return;
                }

                String[] columnNames = {"Passengers", "Flights", "Delayed Flights", "Cancelled Flights", "Successful Flights", "Total Payments"};
                resultTable.setModel(new javax.swing.table.DefaultTableModel(reportData, columnNames));
                scrollPane.setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for back button
        backButton.addActionListener(e -> {
            dispose();
            new GenerateReportsFrame(connection, manageRecord, transaction, report);
        });

        setVisible(true);
    }

    private void initializeDataLists() {
        airportList = new ArrayList<>();

        // Fetch Airports
        String airportQuery = "SELECT airport_id, name FROM airports ORDER BY airport_id ASC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(airportQuery)) {
            while (rs.next()) {
                int id = rs.getInt("airport_id");
                String name = rs.getString("name");
                airportList.add(new AirportEntry(id, name));
                System.out.println("Fetched Airport: " + id + " - " + name); // Debug statement
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching airports: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateParameterComboBox(String reportType) {
        parameterComboBox.removeAllItems();
        if (reportType.equals("By Origin Airport") || reportType.equals("By Destination Airport")) {
            parameterLabel.setText("Select Airport:");
            for (AirportEntry airport : airportList) {
                parameterComboBox.addItem(airport);
            }
        } else {
            parameterLabel.setText("Parameter:");
        }
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
