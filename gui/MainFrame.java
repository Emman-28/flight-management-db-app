package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import operations.*;

public class MainFrame {
    public MainFrame(Connection connection, ManageRecord record, ExecuteTransaction transaction, GenerateReport report) {
        // Main frame setup
        JFrame frame = new JFrame("Flight Database Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize to full screen
        frame.setUndecorated(false); // Set to true if you want no window borders
        frame.setIconImage(new ImageIcon("logo.png").getImage());
        
        // setting background
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
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

        backgroundPanel.setLayout(new GridBagLayout()); // GridBagLayout centers content by default
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing around components

        // Panel to hold the content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false); // Transparent for background visibility
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Title section
        JLabel titleLabel = new JLabel("Flight Database Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        JLabel welcomeLabel = new JLabel("Select an operation", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN,14));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        // Options section (Buttons)
        JButton manageRecordsButton = new JButton("Manage Records");
        JButton executeTransactionsButton = new JButton("Execute Transactions");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton exitSystemButton = new JButton("Exit");

        Dimension buttonSize = new Dimension(250, 40);

        manageRecordsButton.setPreferredSize(buttonSize);
        manageRecordsButton.setMaximumSize(buttonSize);
        manageRecordsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        executeTransactionsButton.setPreferredSize(buttonSize);
        executeTransactionsButton.setMaximumSize(buttonSize);
        executeTransactionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        generateReportsButton.setPreferredSize(buttonSize);
        generateReportsButton.setMaximumSize(buttonSize);
        generateReportsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        exitSystemButton.setPreferredSize(new Dimension(75, 30));
        exitSystemButton.setMaximumSize(new Dimension(75, 30));
        exitSystemButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(manageRecordsButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        contentPanel.add(executeTransactionsButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(generateReportsButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(exitSystemButton);

        // Add action listeners
        manageRecordsButton.addActionListener(e -> {
            frame.dispose();
            new ManageRecordsFrame(connection, record, transaction, report); // Pass all necessary parameters
        });

        executeTransactionsButton.addActionListener(e -> {
            frame.dispose();
            new ExecuteTransactionsFrame(connection, record, transaction, report);
        });

        generateReportsButton.addActionListener(e -> {
            frame.dispose();
            new GenerateReportsFrame(connection, record, transaction, report);
        });

        exitSystemButton.addActionListener(e -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed successfully.");
                }
            } catch (SQLException ex) {
                System.err.println("Failed to close the connection: " + ex.getMessage());
            }
            System.exit(0);
        });

        // Add content panel to the center of the background
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Center in both X and Y axes
        backgroundPanel.add(contentPanel, gbc);

        // Add background to the frame
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }
}
