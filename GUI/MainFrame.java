package GUI;

import operations.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame {

    public MainFrame(ManageRecord record, ExecuteTransaction transaction, GenerateReport report) {
        // Main frame setup
        JFrame frame = new JFrame("Flight Database Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to the Flight Database Management System!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(titlePanel, BorderLayout.NORTH);

        // Options section
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);

        JLabel footerLabel = new JLabel("Select an option to proceed:", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        centerPanel.add(footerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton manageRecordsButton = new JButton("Manage Records");
        JButton executeTransactionsButton = new JButton("Execute Transactions");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton exitSystemButton = new JButton("Exit System");

        Dimension buttonSize = new Dimension(250, 40);
        manageRecordsButton.setPreferredSize(buttonSize);
        executeTransactionsButton.setPreferredSize(buttonSize);
        generateReportsButton.setPreferredSize(buttonSize);
        exitSystemButton.setPreferredSize(buttonSize);

        buttonPanel.add(manageRecordsButton);
        buttonPanel.add(executeTransactionsButton);
        buttonPanel.add(generateReportsButton);
        buttonPanel.add(exitSystemButton);

        // Add action listeners
        manageRecordsButton.addActionListener(e -> {
            frame.dispose();
            new ManageRecordsFrame(record, transaction, report); // Pass all necessary parameters
        });

        executeTransactionsButton.addActionListener(e -> {
            frame.dispose();
            new ExecuteTransactionsFrame(record, transaction, report);
        });

        generateReportsButton.addActionListener(e -> {
            frame.dispose();
            new GenerateReportsFrame(record, transaction, report);
        });

        exitSystemButton.addActionListener(e -> System.exit(0));

        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }
}
