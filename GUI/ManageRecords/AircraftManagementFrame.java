package GUI.ManageRecords;

import operations.ManageRecord;
import operations.ExecuteTransaction;
import operations.GenerateReport;
import GUI.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;

public class AircraftManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;

    public AircraftManagementFrame(ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        setTitle("Aircraft Record Management");
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

        selectionPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space above
        JLabel selectionMessage = new JLabel("Select an action for the aircraft records:", SwingConstants.CENTER);
        selectionMessage.setFont(new Font("Arial", Font.BOLD, 16));
        selectionMessage.setForeground(Color.BLACK);
        selectionMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectionPanel.add(selectionMessage);
        selectionPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space below
        mainPanel.add(selectionPanel, BorderLayout.NORTH);

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        Dimension buttonSize = new Dimension(200, 35);

        JButton createButton = new JButton("Create Aircraft Record");
        createButton.setPreferredSize(buttonSize);
        createButton.addActionListener(e -> showCreateRecordDialog());

        JButton updateButton = new JButton("Update Aircraft Record");
        updateButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for updating records

        JButton readButton = new JButton("Read Aircraft Record");
        readButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for reading records

        JButton deleteButton = new JButton("Delete Aircraft Record");
        deleteButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for deleting records

        JButton backButton = new JButton("Back to Records Menu");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(manageRecord, transaction, report); // Pass all three parameters back
        });

        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(readButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void showCreateRecordDialog() {
        JDialog dialog = new JDialog(this, "Create Aircraft Record", true);
        dialog.setSize(350, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Aircraft ID:");
        JTextField idField = new JTextField();
        JLabel modelLabel = new JLabel("Aircraft Model:");
        JTextField modelField = new JTextField();
        JLabel capacityLabel = new JLabel("Maximum Capacity:");
        JTextField capacityField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(modelLabel);
        inputPanel.add(modelField);
        inputPanel.add(capacityLabel);
        inputPanel.add(capacityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable the create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(!idField.getText().trim().isEmpty() &&
                        !modelField.getText().trim().isEmpty() &&
                        !capacityField.getText().trim().isEmpty());
            }

            public void insertUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        idField.getDocument().addDocumentListener(fieldListener);
        modelField.getDocument().addDocumentListener(fieldListener);
        capacityField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String model = modelField.getText().trim();
            String capacityText = capacityField.getText().trim();

            try {
                int capacity = Integer.parseInt(capacityText);
                manageRecord.create("aircraft", new String[]{"AircraftID", "Model", "MaxCapacity"},
                        new Object[]{id, model, capacity});
                JOptionPane.showMessageDialog(dialog, "Record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid capacity. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
