package gui.ManageRecords;

import gui.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import operations.*;

public class CompanyManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public CompanyManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        setTitle("Company Record Management");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("logo.png").getImage());

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Panel for the selection message with spacing
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBackground(Color.WHITE);

        selectionPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space above
        JLabel selectionMessage = new JLabel("Select an action for the companies records:", SwingConstants.CENTER);
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

        JButton createButton = new JButton("Create Company Record");
        createButton.setPreferredSize(buttonSize);
        createButton.addActionListener(e -> showCreateRecordDialog());

        JButton updateButton = new JButton("Update Company Record");
        updateButton.setPreferredSize(buttonSize);
        updateButton.addActionListener(e -> showUpdateRecordDialog());

        JButton readButton = new JButton("Read Company Record");
        readButton.setPreferredSize(buttonSize);
        readButton.addActionListener(e -> showReadRecordDialog());

        JButton deleteButton = new JButton("Delete Company Record");
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.addActionListener(e -> showDeleteRecordDialog());

        JButton backButton = new JButton("Back to Records Menu");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report); // Pass all three parameters back
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
        JDialog dialog = new JDialog(this, "Create Company Record", true);
        dialog.setSize(600, 380);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10)); // Adjusted for 5 rows
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fetch the next company ID
        int nextCompanyId = getNextCompanyIdFromDatabase();

        // Create components
        JLabel idLabel = new JLabel("Assigned Company ID: " + nextCompanyId);
        JLabel nameLabel = new JLabel("Company Name (25 chars):");
        JTextField nameField = new JTextField();
        JLabel foundedLabel = new JLabel("Date Founded (YYYY-MM-DD):");
        JTextField foundedField = new JTextField();
        JLabel contactLabel = new JLabel("Contact Number (20 digits):");
        JTextField contactField = new JTextField();

        // Add components in the correct order
        inputPanel.add(idLabel); // Assigned ID
        inputPanel.add(new JLabel()); // Empty cell for alignment
        inputPanel.add(nameLabel); // Company Name
        inputPanel.add(nameField);
        inputPanel.add(foundedLabel); // Date Founded
        inputPanel.add(foundedField);
        inputPanel.add(contactLabel); // Contact Number
        inputPanel.add(contactField);

        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable the create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(!nameField.getText().trim().isEmpty() &&
                        !foundedField.getText().trim().isEmpty() &&
                        !contactField.getText().trim().isEmpty());
            }

            public void insertUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        nameField.getDocument().addDocumentListener(fieldListener);
        foundedField.getDocument().addDocumentListener(fieldListener);
        contactField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String companyName = nameField.getText().trim();
            String foundedDate = foundedField.getText().trim();
            String contactStr = contactField.getText().trim();

            try {
                // Validate input
                if (companyName.length() > 25) {
                    throw new IllegalArgumentException("Company Name cannot exceed 25 characters.");
                }

                long contactNumber = Long.parseLong(contactStr);
                if (contactStr.length() > 20) {
                    throw new IllegalArgumentException("Contact Number cannot exceed 20 digits.");
                }

                // Check if company already exists
                if (isCompanyExists(companyName)) {
                    throw new IllegalArgumentException("A company with this name already exists.");
                }

                // Create record in the database
                manageRecord.create("companies", new String[]{"company_id", "name", "date_founded", "contact_number"},
                        new Object[]{nextCompanyId, companyName, Date.valueOf(foundedDate), contactNumber});

                JOptionPane.showMessageDialog(dialog, "Company record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Contact Number must be a valid 20-digit number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(dialog, "Company ID already exists. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error creating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Helper method to check if company exists
    private boolean isCompanyExists(String companyName) throws SQLException {
        String query = "SELECT COUNT(*) FROM companies WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, companyName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if a record exists
                }
            }
        }
        return false;
    }

    // Helper method to get the next available company ID
    private int getNextCompanyIdFromDatabase() {
        String query = "SELECT MAX(company_id) + 1 FROM companies";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Returns the next available company ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default to 1 if no data exists
    }

    private void showUpdateRecordDialog() {
        JDialog dialog = new JDialog(this, "Update Company Record", true);
        dialog.setSize(600, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel companyLabel = new JLabel("Select Company:");
        JComboBox<String> companyDropdown = new JComboBox<>();
        populateCompanyDropdownForUpdate(companyDropdown);

        JLabel nameLabel = new JLabel("Change Company Name (25 chars):");
        JTextField nameField = new JTextField();

        JLabel foundedLabel = new JLabel("Change Date Founded (YYYY-MM-DD):");
        JTextField foundedField = new JTextField();

        JLabel contactLabel = new JLabel("Change Contact Number (20 digits):");
        JTextField contactField = new JTextField();

        // Add components to the input panel
        inputPanel.add(companyLabel);
        inputPanel.add(companyDropdown);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(foundedLabel);
        inputPanel.add(foundedField);
        inputPanel.add(contactLabel);
        inputPanel.add(contactField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        updateButton.setEnabled(false);

        // Variables to hold current values
        final String[] currentCompanyName = {null};
        final String[] currentFoundedDate = {null};
        final Long[] currentContactNumber = {null};
        final String[] currentCompanyId = {null};

        // Populate current values when a company is selected
        companyDropdown.addActionListener(e -> {
            String selectedCompany = (String) companyDropdown.getSelectedItem();
            if (selectedCompany != null && selectedCompany.contains(" - ")) {
                String companyId = selectedCompany.split(" - ")[0].trim();
                currentCompanyId[0] = companyId;

                try (PreparedStatement stmt = connection.prepareStatement(
                        "SELECT name, date_founded, contact_number FROM companies WHERE company_id = ?")) {
                    stmt.setString(1, companyId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            currentCompanyName[0] = rs.getString("name");
                            currentFoundedDate[0] = rs.getString("date_founded");
                            currentContactNumber[0] = rs.getLong("contact_number");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error fetching company details: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                updateButton.setEnabled(true); // Enable the update button once a company is selected
            }
        });

        updateButton.addActionListener(e -> {
            String selectedCompany = (String) companyDropdown.getSelectedItem();
            String newName = nameField.getText().trim();
            String newFoundedDate = foundedField.getText().trim();
            String newContactStr = contactField.getText().trim();

            try {
                if (selectedCompany == null || !selectedCompany.contains(" - ")) {
                    throw new IllegalArgumentException("Invalid company selected.");
                }

                String companyId = selectedCompany.split(" - ")[0].trim();

                // Validate new company name is unique
                if (!newName.isEmpty() && !newName.equals(currentCompanyName[0]) && isCompanyExists(newName)) {
                    throw new IllegalArgumentException("A company with this name already exists.");
                }

                // Validate new contact number
                Long newContactNumber = null;
                if (!newContactStr.isEmpty()) {
                    newContactNumber = Long.parseLong(newContactStr);
                    if (newContactStr.length() > 20) {
                        throw new IllegalArgumentException("Contact Number cannot exceed 20 digits.");
                    }
                }

                // Validate new company name
                if (!newName.isEmpty() && newName.length() > 25) {
                    throw new IllegalArgumentException("Company Name cannot exceed 25 characters.");
                }

                // Validate new founded date
                if (!newFoundedDate.isEmpty()) {
                    try {
                        Date.valueOf(newFoundedDate); // This will throw an exception if the date format is invalid
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD.");
                    }
                }

                // Determine final values to update
                String finalName = newName.isEmpty() ? currentCompanyName[0] : newName;
                String finalFoundedDate = newFoundedDate.isEmpty() ? currentFoundedDate[0] : newFoundedDate;
                Long finalContactNumber = newContactNumber == null ? currentContactNumber[0] : newContactNumber;

                // Update the record
                String[] columns = {"name", "date_founded", "contact_number"};
                Object[] values = {finalName, Date.valueOf(finalFoundedDate), finalContactNumber};
                String condition = "company_id = '" + companyId + "'";

                manageRecord.update("companies", condition, columns, values);

                JOptionPane.showMessageDialog(dialog, "Company record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Contact Number must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Helper method to populate the company dropdown for updating
    private void populateCompanyDropdownForUpdate(JComboBox<String> companyDropdown) {
        String query = "SELECT company_id, name, date_founded FROM companies";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int companyId = rs.getInt("company_id");
                String companyName = rs.getString("name");
                String foundedDate = rs.getString("date_founded");
                companyDropdown.addItem(companyId + " - " + companyName + " - " + foundedDate);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching companies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Placeholder method for Read Record Dialog
    private void showReadRecordDialog() {
        // Placeholder for Read Record Dialog
    }

    // Placeholder method for Delete Record Dialog
    private void showDeleteRecordDialog() {
        // Placeholder for Delete Record Dialog
    }
}
