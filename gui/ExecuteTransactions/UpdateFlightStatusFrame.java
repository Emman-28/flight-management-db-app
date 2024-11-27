package gui.ExecuteTransactions;

import gui.ManageRecordsFrame;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.*;
import operations.ExecuteTransaction;
import operations.GenerateReport;
import operations.ManageRecord;

public class UpdateFlightStatusFrame extends JFrame {

    private final ExecuteTransaction transaction;
    private final ManageRecord manageRecord;
    private final GenerateReport report;
    private final Connection connection;

    public UpdateFlightStatusFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        setTitle("Update Flight Status");
        setSize(500, 500);
        setLocationRelativeTo(null); // centers window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // maximizes window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("logo.png").getImage());

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

        mainPanel.setOpaque(false);

        // content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false); // Transparent
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Padding

        // title
        JLabel titleLabel = new JLabel("Update Flight Status", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // flight id
        JPanel flightIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flightIdPanel.setOpaque(false);
        JLabel flightIdLabel = new JLabel("Flight ID:");
        flightIdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        flightIdLabel.setForeground(Color.BLACK);
        JTextField flightIdField = new JTextField(20);
        flightIdPanel.add(flightIdLabel);
        flightIdPanel.add(flightIdField);
        contentPanel.add(flightIdPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // flight status
        JPanel flightStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flightStatusPanel.setOpaque(false);
        JLabel flightStatusLabel = new JLabel("Flight Status:");
        flightStatusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        flightStatusLabel.setForeground(Color.BLACK);
        String[] statusOptions = {"Scheduled", "Delayed", "Cancelled", "On Air", "Arrived"};
        JComboBox<String> flightStatusComboBox = new JComboBox<>(statusOptions);
        flightStatusComboBox.setSelectedIndex(-1); // No selection initially
        flightStatusPanel.add(flightStatusLabel);
        flightStatusPanel.add(flightStatusComboBox);
        contentPanel.add(flightStatusPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // submit button
        JButton submitButton = createStyledButton("Update Status", new Dimension(150, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(submitButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // back
        JButton backButton = createStyledButton("Back", new Dimension(150, 40));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(backButton);

        // add content
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // main panel
        setContentPane(mainPanel);
        setVisible(true);

        // action listeners
        submitButton.addActionListener(e -> {
            String flightId = flightIdField.getText().trim();
            String flightStatus = (String) flightStatusComboBox.getSelectedItem();

            // input validation
            if (flightId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Flight ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (flightStatus == null || flightStatus.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a Flight Status.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // update
            try {
                transaction.updateFlightStatus(flightId, flightStatus);
                JOptionPane.showMessageDialog(this, "Flight status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage(), "Invalid Status", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database Error: " + sqle.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report); // Navigate back
        });
    }

    private JButton createStyledButton(String text, Dimension size) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }
}
