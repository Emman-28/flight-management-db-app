package gui.GenerateReports;

import gui.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import operations.*;

public class FlightPerformanceFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public FlightPerformanceFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        // Frame settings
        setTitle("Flight Performance Report Generator");
        setSize(500, 400);
        setLocationRelativeTo(null); // Center the window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize to full screen
        setUndecorated(false); // Set to true if you want no window borders
        setIconImage(new ImageIcon("logo.png").getImage());
        
        // Setting background
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

        // Main panel with GridBagLayout
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
        JLabel titleLabel = new JLabel("Flight Performance Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Input fields for month and year
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridy = 1;

        JLabel monthLabel = new JLabel("Month (1-12):");
        mainPanel.add(monthLabel, gbc);

        JTextField monthField = new JTextField(5);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(monthField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;

        JLabel yearLabel = new JLabel("Year:");
        mainPanel.add(yearLabel, gbc);

        JTextField yearField = new JTextField(5);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(yearField, gbc);

        // Generate button
        JButton generateButton = new JButton("Generate");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        generateButton.addActionListener(e -> {
            String monthText = monthField.getText();
            String yearText = yearField.getText();

            try {
                int month = Integer.parseInt(monthText);
                int year = Integer.parseInt(yearText);

                // Validate month and year
                if (month < 1 || month > 12) {
                    throw new NumberFormatException("Month must be between 1 and 12.");
                }

                generateReport(month, year);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid month and year.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(generateButton, gbc);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new GenerateReportsFrame(connection, manageRecord, transaction, report);
        });
        gbc.gridy = 4;
        mainPanel.add(backButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    private void generateReport(int month, int year) {
        String query = """
        SELECT DATE(f.expected_departure_time) AS day,
               COUNT(*) AS total_flights,
               SUM(a.max_capacity) AS total_capacity,
               SUM(a.max_capacity - f.seating_capacity) AS seats_available,
               SUM(f.seating_capacity) AS passengers_flown
        FROM flights f
        JOIN aircrafts a ON f.aircraft_id = a.aircraft_id
        WHERE MONTH(f.expected_departure_time) = ? AND YEAR(f.expected_departure_time) = ?
        GROUP BY DATE(f.expected_departure_time)
        ORDER BY day;
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Date");
                tableModel.addColumn("Total Flights");
                tableModel.addColumn("Total Capacity");
                tableModel.addColumn("Seats Available");
                tableModel.addColumn("Passengers Flown");

                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getDate("day"),
                            rs.getInt("total_flights"),
                            rs.getInt("total_capacity"),
                            rs.getInt("seats_available"),
                            rs.getInt("passengers_flown")
                    });
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No flight data available for the selected month and year.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                JTable reportTable = new JTable(tableModel);
                JScrollPane scrollPane = new JScrollPane(reportTable);
                scrollPane.setPreferredSize(new Dimension(500, 300));

                JOptionPane.showMessageDialog(this, scrollPane, "Flight Performance Report", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching the report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
