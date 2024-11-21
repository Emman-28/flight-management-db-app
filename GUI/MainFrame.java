package GUI;

import javax.swing.*;
import java.awt.*;

public class MainFrame {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Flight Database Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center the window

        // Create a panel for the content
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // Add gaps between components
        panel.setBackground(Color.WHITE); // Set background to white

        // Create and configure the welcome label
        JLabel welcomeLabel = new JLabel("Welcome to the Flight Database Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.BLACK); // Black text for visibility
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Vertical layout for buttons
        buttonPanel.setBackground(Color.WHITE); // Match background

        // Create buttons
        JButton manageRecordsButton = new JButton("Manage Records");
        JButton executeTransactionsButton = new JButton("Execute Transactions");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton exitSystemButton = new JButton("Exit System");

        // Set uniform button size
        int buttonWidth = 250;
        int buttonHeight = 40;
        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        manageRecordsButton.setPreferredSize(buttonSize);
        executeTransactionsButton.setPreferredSize(buttonSize);
        generateReportsButton.setPreferredSize(buttonSize);
        exitSystemButton.setPreferredSize(buttonSize);

        // Add buttons with uniform size and spacing
        buttonPanel.add(manageRecordsButton);
        buttonPanel.add(executeTransactionsButton);
        buttonPanel.add(generateReportsButton);
        buttonPanel.add(exitSystemButton);

        // Add action listeners for the buttons
        manageRecordsButton.addActionListener(e -> {
            frame.dispose();
            new ManageRecordsFrame(); // Open Manage Records window
        });

        executeTransactionsButton.addActionListener(e -> {
            frame.dispose();
            new ExecuteTransactionsFrame(); // Open Execute Transactions window
        });

        generateReportsButton.addActionListener(e -> {
            frame.dispose();
            new GenerateReportsFrame(); // Open Generate Reports window
        });

        exitSystemButton.addActionListener(e -> System.exit(0)); // Exit application

        // Add the button panel to the main panel
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Add a footer message with adjusted spacing
        JLabel footerLabel = new JLabel("Select an option to proceed", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        panel.add(footerLabel, BorderLayout.SOUTH);

        // Add the panel to the frame
        frame.add(panel);

        // Make the frame visible
        frame.setVisible(true);
    }
}
