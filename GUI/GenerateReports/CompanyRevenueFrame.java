package GUI.GenerateReports;

import GUI.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import operations.*;

public class CompanyRevenueFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public CompanyRevenueFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        setTitle("Company Revenue Report Generator");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Panel for the selection message with spacing
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBackground(Color.WHITE);

        // Add the message asking for year selection
        JLabel yearLabel = new JLabel("Select a Year:");
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        selectionPanel.add(yearLabel);

        // Dropdown (JComboBox) for selecting the year
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        // Add some years to the dropdown (can be dynamically generated from the database or hardcoded)
        for (int i = 2020; i <= 2025; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedIndex(0);  // Set default year (e.g., 2020)
        yearComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectionPanel.add(yearComboBox);
        selectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(selectionPanel, BorderLayout.NORTH);

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        Dimension buttonSize = new Dimension(200, 35);

        // Button to generate the report based on selected year
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setPreferredSize(buttonSize);
        generateReportButton.addActionListener(e -> {
            // Get the selected year from the combo box
            int selectedYear = (int) yearComboBox.getSelectedItem();
            try {
                // Call the method to get the company revenue for the selected year
                String reportData = report.companyRevenue(selectedYear, connection);
                // Display the report (for now, just show it in a message dialog)
                JOptionPane.showMessageDialog(this, reportData, "Company Revenue Report", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching the report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(generateReportButton);

        // Button to go back to the records menu
        JButton backButton = new JButton("Back to Records Menu");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report); // Pass all three parameters back
        });

        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }
}
