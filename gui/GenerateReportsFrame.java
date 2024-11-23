package gui;

import gui.GenerateReports.CompanyRevenueFrame;
import gui.GenerateReports.FlightPerformanceFrame;
import gui.GenerateReports.PassengerTrafficFrame;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import operations.*;

public class GenerateReportsFrame {
    public GenerateReportsFrame(Connection connection, ManageRecord record, ExecuteTransaction transaction, GenerateReport report) {
        // Main frame setup
        JFrame frame = new JFrame("Generate Reports");
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
        JLabel titleLabel = new JLabel("Generate Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        JLabel welcomeLabel = new JLabel("Select a report to generate", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN,14));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        // Options section (Buttons)
        JButton passengerButton = new JButton("Passenger Traffic");
        JButton companyButton = new JButton("Company Revenue");
        JButton flightButton = new JButton("Flight Performance");
        JButton backButton = new JButton("Back");

        Dimension buttonSize = new Dimension(250, 40);

        passengerButton.setPreferredSize(buttonSize);
        passengerButton.setMaximumSize(buttonSize);
        passengerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        companyButton.setPreferredSize(buttonSize);
        companyButton.setMaximumSize(buttonSize);
        companyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        flightButton.setPreferredSize(buttonSize);
        flightButton.setMaximumSize(buttonSize);
        flightButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.setPreferredSize(new Dimension(75, 30));
        backButton.setMaximumSize(new Dimension(75, 30));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(passengerButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        contentPanel.add(companyButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(flightButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(backButton);

        // Add action listeners
        passengerButton.addActionListener(e -> {
            frame.dispose();
            new PassengerTrafficFrame(connection, record, transaction, report); // Pass all necessary parameters
        });

        companyButton.addActionListener(e -> {
            frame.dispose();
            new CompanyRevenueFrame(connection, record, transaction, report);
        });

        flightButton.addActionListener(e -> {
            frame.dispose();
            new FlightPerformanceFrame(connection, record, transaction, report);
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            new MainFrame(connection, record, transaction, report);
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
