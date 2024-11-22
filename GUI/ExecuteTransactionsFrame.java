package GUI;

import GUI.ExecuteTransactions.*;
import operations.*;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ExecuteTransactionsFrame {
    public ExecuteTransactionsFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        // Main frame setup
        JFrame frame = new JFrame("Execute Transactions");
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
        JLabel titleLabel = new JLabel("Execute Transactions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        JLabel welcomeLabel = new JLabel("Which transaction would you like to execute?", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements

        // Options section (Buttons)
        JButton bookFlightButton = new JButton("Book a Flight");
        JButton refundBookingButton = new JButton("Refund a Booking");
        JButton rescheduleBookingButton = new JButton("Reschedule a Booking");
        JButton updateFlightButton = new JButton("Update a Flight");

        Dimension buttonSize = new Dimension(250, 40);

        bookFlightButton.setPreferredSize(buttonSize);
        bookFlightButton.setMaximumSize(buttonSize);
        bookFlightButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        refundBookingButton.setPreferredSize(buttonSize);
        refundBookingButton.setMaximumSize(buttonSize);
        refundBookingButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        rescheduleBookingButton.setPreferredSize(buttonSize);
        rescheduleBookingButton.setMaximumSize(buttonSize);
        rescheduleBookingButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        updateFlightButton.setPreferredSize(buttonSize);
        updateFlightButton.setMaximumSize(buttonSize);
        updateFlightButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(bookFlightButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        contentPanel.add(refundBookingButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(rescheduleBookingButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(updateFlightButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(75, 30));
        backButton.setMaximumSize(new Dimension(75, 30));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(backButton);

        // Add action listeners
        bookFlightButton.addActionListener(e -> {
            frame.dispose();
            new BookFlightFrame(connection, manageRecord, transaction, report); // Redirect to BookFlightFrame
        });

        refundBookingButton.addActionListener(e -> {
            frame.dispose();
            new RefundBookingFrame(connection, manageRecord, transaction, report); // Redirect to RefundBookingFrame
        });

        rescheduleBookingButton.addActionListener(e -> {
            frame.dispose();
            new RescheduleBookingFrame(connection, manageRecord, transaction, report); // Redirect to RescheduleBookingFrame
        });

        updateFlightButton.addActionListener(e -> {
            frame.dispose();
            new UpdateFlightFrame(connection, manageRecord, transaction, report); // Redirect to UpdateFlightFrame
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            new MainFrame(connection, manageRecord, transaction, report); // Back to MainFrame
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
