package gui;

import gui.ManageRecords.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import operations.*;

public class ManageRecordsFrame {
    public ManageRecordsFrame(Connection connection, ManageRecord record, ExecuteTransaction transaction, GenerateReport report) {
        // Main frame setup
        JFrame frame = new JFrame("Manage Records");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize to full screen
        frame.setUndecorated(false); // Set to true if you want no window borders
        frame.setIconImage(new ImageIcon("logo.png").getImage());

        // Setting background
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
        JLabel titleLabel = new JLabel("Manage Records", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        JLabel welcomeLabel = new JLabel("Select a record to manage", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        // Options section (Buttons)
        JButton airportButton = new JButton("Airport Records");
        JButton flightButton = new JButton("Flight Records");
        JButton flightLogButton = new JButton("Flight Log Management");
        JButton passportButton = new JButton("Passport Records");
        JButton companyButton = new JButton("Company Records");
        JButton aircraftButton = new JButton("Aircraft Records");

        Dimension buttonSize = new Dimension(250, 40);

        airportButton.setPreferredSize(buttonSize);
        airportButton.setMaximumSize(buttonSize);
        airportButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        flightButton.setPreferredSize(buttonSize);
        flightButton.setMaximumSize(buttonSize);
        flightButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        flightLogButton.setPreferredSize(buttonSize);
        flightLogButton.setMaximumSize(buttonSize);
        flightLogButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        passportButton.setPreferredSize(buttonSize);
        passportButton.setMaximumSize(buttonSize);
        passportButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        companyButton.setPreferredSize(buttonSize);
        companyButton.setMaximumSize(buttonSize);
        companyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        aircraftButton.setPreferredSize(buttonSize);
        aircraftButton.setMaximumSize(buttonSize);
        aircraftButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(aircraftButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(airportButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        contentPanel.add(companyButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(flightButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(flightLogButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(passportButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(75, 30));
        backButton.setMaximumSize(new Dimension(75, 30));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(backButton);

        // Add action listeners
        airportButton.addActionListener(e -> {
            frame.dispose();
            new AirportManagementFrame(connection, record, transaction, report); // Redirect to Airport Management
        });

        flightButton.addActionListener(e -> {
            frame.dispose();
            new FlightManagementFrame(connection, record, transaction, report); // Redirect to Flight Management
        });
        
        flightLogButton.addActionListener(e -> {
            frame.dispose();
            new FlightLogManagementFrame(connection, record, transaction, report); // Redirect to Flight Log Management
        });


        passportButton.addActionListener(e -> {
            frame.dispose();
            new PassportManagementFrame(connection, record, transaction, report); // Redirect to Passport Management
        });

        companyButton.addActionListener(e -> {
            frame.dispose();
            new CompanyManagementFrame(connection, record, transaction, report); // Redirect to Company Management
        });

        aircraftButton.addActionListener(e -> {
            frame.dispose();
            new AircraftManagementFrame(connection, record, transaction, report); // Redirect to Aircraft Management
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            new MainFrame(connection, record, transaction, report); // Back to the Main Frame
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
