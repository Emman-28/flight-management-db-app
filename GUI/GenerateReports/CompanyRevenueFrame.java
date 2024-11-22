package GUI.GenerateReports;

import GUI.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import javax.imageio.ImageIO;
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

        // frame settings
        setTitle("Company Revenue Report Generator");
        setSize(500, 500);
        setLocationRelativeTo(null); // centers window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // maximizes window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("logo.jpg").getImage());

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

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        selectionPanel.setOpaque(false); // Make selection panel transparent
        JLabel promptLabel = new JLabel("Generating Company Revenue Report...");
        promptLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        selectionPanel.add(yearLabel);

        // populating dropdown for year selection
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT YEAR(booking_date) FROM bookings ORDER BY YEAR(booking_date) DESC")) {
                while (rs.next()) {
                yearComboBox.addItem(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading years from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        yearComboBox.setSelectedIndex(0);
        selectionPanel.add(yearComboBox);

        gbc.gridy = 1;
        mainPanel.add(promptLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make button panel transparent
        buttonPanel.setLayout(new GridBagLayout());
        Dimension buttonSize = new Dimension(100, 30);

        GridBagConstraints gbcbtn = new GridBagConstraints();
        gbcbtn.insets = new Insets(0, 0, 10, 0);
        gbcbtn.gridx = 0;
        gbcbtn.gridy = 0;
        gbcbtn.anchor = GridBagConstraints.CENTER;

        JButton generateReportButton = new JButton("Generate");
        generateReportButton.setPreferredSize(buttonSize);
        generateReportButton.addActionListener(e -> {
            int selectedYear = (int) yearComboBox.getSelectedItem();
            try {
                Object[][] reportData = report.companyRevenue(selectedYear, connection);
            
                if (reportData == null || reportData.length == 0) {
                    JOptionPane.showMessageDialog(this, "No data available for the selected year.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            
                String[] columnNames = {"Year", "Company Name", "Revenue"};  // Updated column names
            
                JTable resultTable = new JTable(reportData, columnNames);
                resultTable.setFillsViewportHeight(true);  // Make the table fill the viewport
                JScrollPane scrollPane = new JScrollPane(resultTable);
            
                JOptionPane.showMessageDialog(this, scrollPane, "Company Revenue Report", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching the report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            dispose();
            new GenerateReportsFrame(connection, manageRecord, transaction, report);
        });

        gbcbtn.gridy = 0;
        buttonPanel.add(generateReportButton, gbcbtn);

        gbcbtn.gridy = 1;
        buttonPanel.add(backButton, gbcbtn);

        gbc.gridy = 2;
        mainPanel.add(selectionPanel, gbc);

        gbc.gridy = 3;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }
}
