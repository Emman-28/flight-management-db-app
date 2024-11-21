package GUI.ManageRecords;

import operations.*;
import GUI.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;

public class AirportManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;

    public AirportManagementFrame(ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        setTitle("Airport Record Management");
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
        JLabel selectionMessage = new JLabel("Select an action for the airport records:", SwingConstants.CENTER);
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

        JButton createButton = new JButton("Create Airport Record");
        createButton.setPreferredSize(buttonSize);
        createButton.addActionListener(e -> showCreateRecordDialog());

        JButton updateButton = new JButton("Update Airport Record");
        updateButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for updating records

        JButton readButton = new JButton("Read Airport Record");
        readButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for reading records

        JButton deleteButton = new JButton("Delete Airport Record");
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
        JDialog dialog = new JDialog(this, "Create Airport Record", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Airport ID (11 chars):");
        JTextField idField = new JTextField();
        JLabel nameLabel = new JLabel("Name (25 chars):");
        JTextField nameField = new JTextField();
        JLabel countryLabel = new JLabel("Country (25 chars):");
        JTextField countryField = new JTextField();
        JLabel companyLabel = new JLabel("Company ID (11 chars):");
        JTextField companyField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(countryLabel);
        inputPanel.add(countryField);
        inputPanel.add(companyLabel);
        inputPanel.add(companyField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable the create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(!idField.getText().trim().isEmpty() &&
                        !nameField.getText().trim().isEmpty() &&
                        !countryField.getText().trim().isEmpty() &&
                        !companyField.getText().trim().isEmpty());
            }

            public void insertUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        idField.getDocument().addDocumentListener(fieldListener);
        nameField.getDocument().addDocumentListener(fieldListener);
        countryField.getDocument().addDocumentListener(fieldListener);
        companyField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String airportId = idField.getText().trim();
            String name = nameField.getText().trim();
            String country = countryField.getText().trim();
            String companyId = companyField.getText().trim();

            try {
                if (airportId.length() > 11 || name.length() > 25 || country.length() > 25 || companyId.length() > 11) {
                    throw new IllegalArgumentException("Input length exceeds allowed character limits.");
                }

                int companyIdInt = Integer.parseInt(companyId); // Validate numeric input
                manageRecord.create("airport", new String[]{"AirportID", "Name", "Country", "CompanyID"},
                        new Object[]{airportId, name, country, companyIdInt});
                JOptionPane.showMessageDialog(dialog, "Record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Company ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(dialog, "Airport ID already exists. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error creating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
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
