package GUI.ManageRecords;

import operations.FlightLogManager;
import operations.ManageRecord;
import operations.ExecuteTransaction;
import operations.GenerateReport;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class FlightLogManagementFrame extends JFrame {

    private final FlightLogManager flightLogManager;

    public FlightLogManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        setTitle("Flight Log Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.flightLogManager = new FlightLogManager(connection);

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.WHITE);

        // prompt panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Select a Flight Log Operation:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        instructionPanel.add(instructionLabel);

        // buytton panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setBackground(Color.WHITE);
        Dimension buttonSize = new Dimension(200, 40);

        JButton addFlightLogButton = new JButton("Add Flight Log");
        JButton updateFlightLogButton = new JButton("Update Flight Log");
        JButton deleteFlightLogButton = new JButton("Delete Flight Log");
        JButton viewAllFlightLogsButton = new JButton("View All Flight Logs");

        addFlightLogButton.setPreferredSize(buttonSize);
        updateFlightLogButton.setPreferredSize(buttonSize);
        deleteFlightLogButton.setPreferredSize(buttonSize);
        viewAllFlightLogsButton.setPreferredSize(buttonSize);

        // adds action listeners
        addFlightLogButton.addActionListener(e -> addFlightLog());
        updateFlightLogButton.addActionListener(e -> updateFlightLog());
        deleteFlightLogButton.addActionListener(e -> deleteFlightLog());
        viewAllFlightLogsButton.addActionListener(e -> viewAllFlightLogs());

        buttonPanel.add(addFlightLogButton);
        buttonPanel.add(updateFlightLogButton);
        buttonPanel.add(deleteFlightLogButton);
        buttonPanel.add(viewAllFlightLogsButton);

        // back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        bottomPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report); // Back to ManageRecordsFrame
        });
        bottomPanel.add(backButton);

        // adss components to main panel
        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // add log
    private void addFlightLog() {
        try {
            int logId = Integer.parseInt(JOptionPane.showInputDialog("Enter Log ID:"));
            String flightId = JOptionPane.showInputDialog("Enter Flight ID:");
            String logDate = JOptionPane.showInputDialog("Enter Log Date (YYYY-MM-DD HH:MM:SS):");
            int eventTypeId = Integer.parseInt(JOptionPane.showInputDialog("Enter Event Type ID:"));

            flightLogManager.addFlightLog(logId, flightId, logDate, eventTypeId);
            JOptionPane.showMessageDialog(this, "Flight log added successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // update log
    private void updateFlightLog() {
        try {
            int logId = Integer.parseInt(JOptionPane.showInputDialog("Enter Log ID to Update:"));
            String[] updateOptions = {"Update Flight ID", "Update Log Date", "Update Event Type ID"};
            int updateChoice = JOptionPane.showOptionDialog(this,
                    "What would you like to update?",
                    "Update Flight Log",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    updateOptions,
                    updateOptions[0]);

            if (updateChoice == 0) {
                String newFlightId = JOptionPane.showInputDialog("Enter New Flight ID:");
                flightLogManager.updateFlightLog(logId, new String[]{"flight_id"}, new Object[]{newFlightId});
                JOptionPane.showMessageDialog(this, "Flight ID updated successfully!");
            } else if (updateChoice == 1) {
                String newLogDate = JOptionPane.showInputDialog("Enter New Log Date (YYYY-MM-DD HH:MM:SS):");
                flightLogManager.updateFlightLog(logId, new String[]{"log_date"}, new Object[]{newLogDate});
                JOptionPane.showMessageDialog(this, "Log date updated successfully!");
            } else if (updateChoice == 2) {
                int newEventTypeId = Integer.parseInt(JOptionPane.showInputDialog("Enter New Event Type ID:"));
                flightLogManager.updateFlightLog(logId, new String[]{"event_type_id"}, new Object[]{newEventTypeId});
                JOptionPane.showMessageDialog(this, "Event Type ID updated successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // delete log
    private void deleteFlightLog() {
        try {
            int logId = Integer.parseInt(JOptionPane.showInputDialog("Enter Log ID to Delete:"));
            flightLogManager.deleteFlightLog(logId);
            JOptionPane.showMessageDialog(this, "Flight log deleted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // view logss
    private void viewAllFlightLogs() {
        try {
            String result = flightLogManager.viewAllFlightLogs();
            JTextArea textArea = new JTextArea(result);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 400));
            JOptionPane.showMessageDialog(this, scrollPane, "All Flight Log Records", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
