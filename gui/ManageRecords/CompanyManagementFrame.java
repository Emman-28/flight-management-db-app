package gui.ManageRecords;

import gui.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    private void showReadRecordDialog() {
        JDialog dialog = new JDialog(this, "Read Company Records", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton filterButton = new JButton("Use Filters");
        JButton inputButton = new JButton("Use Input");
        JButton cancelButton = new JButton("Cancel");

        filterButton.setPreferredSize(new Dimension(200, 35));
        inputButton.setPreferredSize(new Dimension(200, 35));
        cancelButton.setPreferredSize(new Dimension(200, 35));

        filterButton.addActionListener(e -> {
            dialog.dispose();
            showFilterDialog();  // This will call the company filter dialog
        });

        inputButton.addActionListener(e -> {
            dialog.dispose();
            showReadInputDialog();  // This will call the company input dialog
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(filterButton);
        buttonPanel.add(inputButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showReadInputDialog() {
        JDialog dialog = new JDialog(this, "Read Company Record via Input", true);
        dialog.setSize(1000, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10)); // Adjusted rows for the new toggle
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels and text fields
        JLabel companyIdLabel = new JLabel("Company ID (Single or Range, e.g., 1 or 1-10):");
        JTextField companyIdField = new JTextField();

        JLabel companyNameLabel = new JLabel("Company Name (Single or Comma-separated, e.g., ABC Corp, XYZ Ltd):");
        JTextField companyNameField = new JTextField();

        // Toggle for LIKE query
        JCheckBox likeQueryToggle = new JCheckBox("Use LIKE for Company Name (Supports Wildcards, e.g., %Corp%)");

        JLabel dateFoundedLabel = new JLabel("Date Founded (Text or Range, e.g., 2000, 1990-2000, May):");
        JTextField dateFoundedField = new JTextField();

        JLabel contactNumberLabel = new JLabel("Contact Number (Text):");
        JTextField contactNumberField = new JTextField();

        inputPanel.add(companyIdLabel);
        inputPanel.add(companyIdField);
        inputPanel.add(companyNameLabel);
        inputPanel.add(companyNameField);
        inputPanel.add(new JLabel()); // Empty label for spacing
        inputPanel.add(likeQueryToggle); // Add toggle below the Company Name field
        inputPanel.add(dateFoundedLabel);
        inputPanel.add(dateFoundedField);
        inputPanel.add(contactNumberLabel);
        inputPanel.add(contactNumberField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        searchButton.addActionListener(e -> {
            try {
                // Build WHERE clause based on input fields
                StringBuilder whereClause = new StringBuilder();

                // Parse Company ID (single or range)
                if (!companyIdField.getText().trim().isEmpty()) {
                    String companyIdInput = companyIdField.getText().trim();
                    if (companyIdInput.contains("-")) {
                        String[] range = companyIdInput.split("-");
                        whereClause.append("company_id BETWEEN ")
                                .append(range[0].trim())
                                .append(" AND ")
                                .append(range[1].trim())
                                .append(" AND ");
                    } else {
                        whereClause.append("company_id = ").append(companyIdInput).append(" AND ");
                    }
                }

                // Parse Company Name with optional LIKE query
                if (!companyNameField.getText().trim().isEmpty()) {
                    String[] companyNames = companyNameField.getText().trim().split(",");
                    whereClause.append("(");
                    for (String name : companyNames) {
                        if (likeQueryToggle.isSelected()) {
                            whereClause.append("name LIKE '").append(name.trim()).append("' OR ");
                        } else {
                            whereClause.append("name = '").append(name.trim()).append("' OR ");
                        }
                    }
                    whereClause.setLength(whereClause.length() - 4); // Remove the last " OR "
                    whereClause.append(") AND ");
                }

                // Parse Date Founded (single or range or month)
                if (!dateFoundedField.getText().trim().isEmpty()) {
                    String dateFoundedInput = dateFoundedField.getText().trim();

                    if (dateFoundedInput.contains("-")) { // Range of years (YYYY-YYYY)
                        String[] range = dateFoundedInput.split("-");
                        whereClause.append("date_founded BETWEEN '")
                                .append(range[0].trim())
                                .append("-01-01' AND '")
                                .append(range[1].trim())
                                .append("-12-31' AND ");
                    } else if (dateFoundedInput.matches("\\d{4}")) { // Single year (YYYY)
                        whereClause.append("date_founded BETWEEN '")
                                .append(dateFoundedInput)
                                .append("-01-01' AND '")
                                .append(dateFoundedInput)
                                .append("-12-31' AND ");
                    } else if (dateFoundedInput.matches("\\d{4}-\\d{2}-\\d{2}")) { // Exact date (YYYY-MM-DD)
                        whereClause.append("date_founded = '")
                                .append(dateFoundedInput)
                                .append("' AND ");
                    } else { // Month name (e.g., January, March)
                        String month = dateFoundedInput.trim().toLowerCase();
                        whereClause.append("MONTHNAME(date_founded) = '")
                                .append(month.substring(0, 1).toUpperCase())
                                .append(month.substring(1)) // Capitalize the first letter
                                .append("' AND ");
                    }
                }

                // Parse Contact Number
                if (!contactNumberField.getText().trim().isEmpty()) {
                    whereClause.append("contact_number = '").append(contactNumberField.getText().trim()).append("' AND ");
                }

                // Remove the last " AND " if the clause exists
                if (whereClause.length() > 0) {
                    whereClause.setLength(whereClause.length() - 5);
                }

                // Construct the final query
                String query = whereClause.length() > 0 ? whereClause.toString() : null;
                List<Object[]> results;
                List<String> columnNames = List.of("Company ID", "Company Name", "Date Founded", "Contact Number");

                if (query == null || query.isEmpty()) {
                    results = manageRecord.readWithQuery("SELECT * FROM companies");
                } else {
                    results = manageRecord.readWithQuery("SELECT * FROM companies WHERE " + query);
                }

                // Handle case with no matching records
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "No records found matching the query conditions.", "No Records Found", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Prepare data for JTable
                    Object[][] data = new Object[results.size()][columnNames.size()];
                    for (int i = 0; i < results.size(); i++) {
                        data[i] = results.get(i);
                    }

                    // Create JTable for displaying results
                    JTable resultTable = new JTable(data, columnNames.toArray());
                    JScrollPane scrollPane = new JScrollPane(resultTable);
                    JOptionPane.showMessageDialog(dialog, scrollPane, "Query Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showFilterDialog() {
        // Create dialog with appropriate title and size
        JDialog dialog = new JDialog(this, "Read Company Records via Filters", true);
        dialog.setSize(700, 450); // Adjust size as needed
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Create a panel for the selections
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        GridBagConstraints gbc = new GridBagConstraints();
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set GridBagLayout constraints
        gbc.insets = new Insets(5, 10, 5, 10); // Adds padding between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Labels for information and order by
        JLabel includeLabel = new JLabel("<html><body>Select Company Information to Include (min. 2):</body></html>");
        JLabel orderByLabel = new JLabel("Order By (max. 1):");
        JLabel orderAdviceLabel = new JLabel("<html><body><i>Note: Default arrangement is ascending.</i></body></html>");

        // Positioning labels
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        selectionPanel.add(includeLabel, gbc);
        gbc.gridx = 2; gbc.gridwidth = 2;
        selectionPanel.add(orderByLabel, gbc);
        gbc.gridy = 5; gbc.gridwidth = 2;
        selectionPanel.add(orderAdviceLabel, gbc);

        // Info checkboxes
        JCheckBox companyIdCheckbox = new JCheckBox("Company ID");
        JCheckBox companyNameCheckbox = new JCheckBox("Company Name");
        JCheckBox dateFoundedCheckbox = new JCheckBox("Date Founded");
        JCheckBox contactNumberCheckbox = new JCheckBox("Contact Number");

        // "All" checkbox placed below the other checkboxes
        JCheckBox allCheckbox = new JCheckBox("All");

        // Order by checkboxes (initially disabled)
        JCheckBox orderByCompanyId = new JCheckBox("Company ID");
        JCheckBox orderByCompanyName = new JCheckBox("Company Name");
        JCheckBox orderByDateFounded = new JCheckBox("Date Founded");
        JCheckBox orderByContactNumber = new JCheckBox("Contact Number");
        JCheckBox descendingOrderCheckbox = new JCheckBox("Descending Order"); // New checkbox

        // Initially disable order-by checkboxes
        orderByCompanyId.setEnabled(false);
        orderByCompanyName.setEnabled(false);
        orderByDateFounded.setEnabled(false);
        orderByContactNumber.setEnabled(false);
        descendingOrderCheckbox.setEnabled(false);

        // Add info checkboxes to the panel
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        selectionPanel.add(companyIdCheckbox, gbc);
        gbc.gridy = 2;
        selectionPanel.add(companyNameCheckbox, gbc);
        gbc.gridy = 3;
        selectionPanel.add(dateFoundedCheckbox, gbc);
        gbc.gridy = 4;
        selectionPanel.add(contactNumberCheckbox, gbc);

        // Add order-by checkboxes to the panel
        gbc.gridx = 2; gbc.gridy = 1;
        selectionPanel.add(orderByCompanyId, gbc);
        gbc.gridy = 2;
        selectionPanel.add(orderByCompanyName, gbc);
        gbc.gridy = 3;
        selectionPanel.add(orderByDateFounded, gbc);
        gbc.gridy = 4;
        selectionPanel.add(orderByContactNumber, gbc);
        gbc.gridy = 6; // Positioning the descending order checkbox
        selectionPanel.add(descendingOrderCheckbox, gbc);

        // Add "All" checkbox
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        selectionPanel.add(allCheckbox, gbc);

        // Read and Cancel Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton readButton = new JButton("Read");
        JButton cancelButton = new JButton("Cancel");
        readButton.setEnabled(false);

        // Logic to enable/disable buttons and checkboxes
        ActionListener checkboxListener = e -> {
            int selectedInfoCount = (companyIdCheckbox.isSelected() ? 1 : 0) +
                    (companyNameCheckbox.isSelected() ? 1 : 0) +
                    (dateFoundedCheckbox.isSelected() ? 1 : 0) +
                    (contactNumberCheckbox.isSelected() ? 1 : 0);

            int selectedOrderByCount = (orderByCompanyId.isSelected() ? 1 : 0) +
                    (orderByCompanyName.isSelected() ? 1 : 0) +
                    (orderByDateFounded.isSelected() ? 1 : 0) +
                    (orderByContactNumber.isSelected() ? 1 : 0);

            // Enable/disable the Read button based on selection criteria
            readButton.setEnabled(selectedInfoCount >= 2 && selectedOrderByCount == 1);

            // Enable order-by checkboxes only if corresponding info checkbox is selected
            orderByCompanyId.setEnabled(companyIdCheckbox.isSelected());
            orderByCompanyName.setEnabled(companyNameCheckbox.isSelected());
            orderByDateFounded.setEnabled(dateFoundedCheckbox.isSelected());
            orderByContactNumber.setEnabled(contactNumberCheckbox.isSelected());

            // Untick order-by checkboxes if corresponding info checkbox is unticked
            if (!companyIdCheckbox.isSelected()) orderByCompanyId.setSelected(false);
            if (!companyNameCheckbox.isSelected()) orderByCompanyName.setSelected(false);
            if (!dateFoundedCheckbox.isSelected()) orderByDateFounded.setSelected(false);
            if (!contactNumberCheckbox.isSelected()) orderByContactNumber.setSelected(false);

            // Enable descending order checkbox if any order-by checkbox is selected
            descendingOrderCheckbox.setEnabled(selectedOrderByCount > 0);

            // Update "All" checkbox status based on the other checkboxes
            allCheckbox.setSelected(companyIdCheckbox.isSelected() &&
                    companyNameCheckbox.isSelected() &&
                    dateFoundedCheckbox.isSelected() &&
                    contactNumberCheckbox.isSelected());
        };

        // Add listeners to all checkboxes
        companyIdCheckbox.addActionListener(checkboxListener);
        companyNameCheckbox.addActionListener(checkboxListener);
        dateFoundedCheckbox.addActionListener(checkboxListener);
        contactNumberCheckbox.addActionListener(checkboxListener);
        orderByCompanyId.addActionListener(checkboxListener);
        orderByCompanyName.addActionListener(checkboxListener);
        orderByDateFounded.addActionListener(checkboxListener);
        orderByContactNumber.addActionListener(checkboxListener);

        // All checkbox logic
        allCheckbox.addActionListener(e -> {
            boolean isSelected = allCheckbox.isSelected();
            companyIdCheckbox.setSelected(isSelected);
            companyNameCheckbox.setSelected(isSelected);
            dateFoundedCheckbox.setSelected(isSelected);
            contactNumberCheckbox.setSelected(isSelected);

            // Disable other checkboxes when "All" is selected
            companyIdCheckbox.setEnabled(!isSelected);
            companyNameCheckbox.setEnabled(!isSelected);
            dateFoundedCheckbox.setEnabled(!isSelected);
            contactNumberCheckbox.setEnabled(!isSelected);

            // Update the checkbox listener manually for all info checkboxes
            checkboxListener.actionPerformed(null);
        });

        // Read Button Logic
        readButton.addActionListener(e -> {
            List<String> columns = new ArrayList<>();

            // Check selected columns for company info
            if (companyIdCheckbox.isSelected()) columns.add("company_id");
            if (companyNameCheckbox.isSelected()) columns.add("name");
            if (dateFoundedCheckbox.isSelected()) columns.add("date_founded");
            if (contactNumberCheckbox.isSelected()) columns.add("contact_number");

            // Construct SELECT query
            StringBuilder query = new StringBuilder("SELECT ");
            query.append(String.join(", ", columns)).append(" FROM companies");

            // Add ORDER BY clause if selected
            if (orderByCompanyId.isSelected()) query.append(" ORDER BY company_id");
            else if (orderByCompanyName.isSelected()) query.append(" ORDER BY name");
            else if (orderByDateFounded.isSelected()) query.append(" ORDER BY date_founded");
            else if (orderByContactNumber.isSelected()) query.append(" ORDER BY contact_number");

            // Add descending order if checkbox is selected
            if (descendingOrderCheckbox.isSelected()) query.append(" DESC");

            try {
                // Execute query
                List<Object[]> results = manageRecord.readWithQuery(query.toString());

                // Prepare data for JTable
                String[] columnNames = columns.toArray(new String[0]);
                Object[][] data = new Object[results.size()][columns.size()];
                for (int i = 0; i < results.size(); i++) {
                    Object[] row = results.get(i);
                    for (int j = 0; j < row.length; j++) {
                        data[i][j] = row[j];
                    }
                }

                // Display results in a JTable
                JTable resultTable = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(resultTable);
                JOptionPane.showMessageDialog(dialog, scrollPane, "Query Results", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error executing query: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel Button Logic
        cancelButton.addActionListener(e -> dialog.dispose());

        // Add buttons to the button panel
        buttonPanel.add(readButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        dialog.add(selectionPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteRecordDialog() {
        JDialog dialog = new JDialog(this, "Delete Company Record", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Panel for the table
        JPanel tablePanel = new JPanel(new BorderLayout());

        // SQL query to fetch company data
        String query = "SELECT company_id, name, contact_number FROM companies";

        // Fetch company records using the readWithQuery method
        List<Object[]> companyData;
        try {
            companyData = manageRecord.readWithQuery(query);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error fetching company records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Columns for the table
        String[] columnNames = {"Company ID", "Name", "Contact Number"};

        // Convert List<Object[]> to a 2D array for the table data
        Object[][] data = new Object[companyData.size()][3];
        for (int i = 0; i < companyData.size(); i++) {
            data[i] = companyData.get(i);
        }

        // Create the table to display company records
        JTable companyTable = new JTable(data, columnNames);
        companyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection
        JScrollPane tableScrollPane = new JScrollPane(companyTable);

        // Add the table to the tablePanel
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create the Delete and Cancel buttons
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        deleteButton.setEnabled(false); // Initially disabled, will be enabled when a row is selected

        // Enable delete button when a row is selected
        companyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && companyTable.getSelectedRow() != -1) {
                deleteButton.setEnabled(true);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = companyTable.getSelectedRow();
            if (selectedRow != -1) {
                Object companyIdObj = companyTable.getValueAt(selectedRow, 0); // Get the company_id from selected row
                String companyId = companyIdObj.toString(); // Convert it to String

                int confirmation = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete Company ID: " + companyId + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        // Build the condition string to match the selected company_id
                        String condition = "company_id = '" + companyId + "'";
                        // Call delete method from manageRecord class with the condition
                        manageRecord.delete("companies", condition);
                        JOptionPane.showMessageDialog(dialog, "Company deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Error deleting company: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Add buttons to buttonPanel
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        dialog.add(tablePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
