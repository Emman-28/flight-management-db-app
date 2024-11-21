package GUI.ManageRecords;

import GUI.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import operations.*;

public class AirportManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public AirportManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
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
        JLabel selectionMessage = new JLabel("Select an action for the airports records:", SwingConstants.CENTER);
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

        JButton createButton = new JButton("Create Airports Record");
        createButton.setPreferredSize(buttonSize);
        createButton.addActionListener(e -> showCreateRecordDialog());

        JButton updateButton = new JButton("Update Airports Record");
        updateButton.setPreferredSize(buttonSize);
        updateButton.addActionListener(e -> showUpdateDialog());

        JButton readButton = new JButton("Read Airports Record");
        readButton.setPreferredSize(buttonSize);
        readButton.addActionListener(e -> showReadRecordDialog());

        JButton deleteButton = new JButton("Delete Airports Record");
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
        JDialog dialog = new JDialog(this, "Create Airports Record", true);
        dialog.setSize(600, 380);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10)); // Adjusted for 5 rows
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fetch the next airport ID
        int nextAirportId = getNextAirportIdFromDatabase();

        // Create components
        JLabel idLabel = new JLabel("Assigned Airport ID: " + nextAirportId);
        JLabel nameLabel = new JLabel("Name (25 chars):");
        JTextField nameField = new JTextField();
        JLabel countryLabel = new JLabel("Country (25 chars):");
        JTextField countryField = new JTextField();
        JLabel companyLabel = new JLabel("Select Company:");

        // Create company dropdown
        JComboBox<String> companyDropdown = new JComboBox<>();
        populateCompanyDropdown(companyDropdown); // Method to load companies from the database

        // Add components in the correct order
        inputPanel.add(idLabel); // Assigned ID
        inputPanel.add(new JLabel()); // Empty cell for alignment
        inputPanel.add(nameLabel); // Airport Name
        inputPanel.add(nameField);
        inputPanel.add(countryLabel); // Country Name
        inputPanel.add(countryField);
        inputPanel.add(companyLabel); // Select Company
        inputPanel.add(companyDropdown);

        // Add an info label at the bottom
        JLabel infoLabel = new JLabel("<html><i>If desired company is missing from the options, proceed to Company Record Management.</i></html>");
        infoLabel.setForeground(Color.GRAY);
        inputPanel.add(new JLabel()); // Empty cell for alignment
        inputPanel.add(infoLabel);

        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable the create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(!nameField.getText().trim().isEmpty() &&
                        !countryField.getText().trim().isEmpty() &&
                        companyDropdown.getSelectedItem() != null);
            }

            public void insertUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        nameField.getDocument().addDocumentListener(fieldListener);
        countryField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String country_name = countryField.getText().trim();
            String selectedCompany = (String) companyDropdown.getSelectedItem();

            try {
                if (selectedCompany == null || !selectedCompany.contains(" - ")) {
                    throw new IllegalArgumentException("Invalid company selected.");
                }
                int company_id = Integer.parseInt(selectedCompany.split(" - ")[0].trim());

                if (name.length() > 25 || country_name.length() > 25) {
                    throw new IllegalArgumentException("Input length exceeds allowed character limits.");
                }

                manageRecord.create("airports", new String[]{"airport_id", "name", "country_name", "company_id"},
                        new Object[]{nextAirportId, name, country_name, company_id});
                JOptionPane.showMessageDialog(dialog, "Record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(dialog, "Airports ID already exists. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
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


    private int getNextAirportIdFromDatabase() {
        String query = "SELECT MAX(airport_id) AS max_id FROM airports";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching next Airport ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 1; // Default to 1 if the table is empty or an error occurs
    }

    // Helper method to populate the company dropdown
    private void populateCompanyDropdown(JComboBox<String> companyDropdown) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT company_id, name FROM companies")) {
            while (rs.next()) {
                String companyId = rs.getString("company_id");
                String companyName = rs.getString("name");
                companyDropdown.addItem(companyId + " - " + companyName); // Display both ID and name
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching companies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateAirportDropdown(Connection connection, JComboBox<String> airportDropdown) {
        String query = "SELECT a.airport_id, a.name, a.country_name, a.company_id " +
                "FROM airports a";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear existing items in the dropdown
            airportDropdown.removeAllItems();

            // Populate the dropdown with the fetched results
            while (rs.next()) {
                String airportId = rs.getString("airport_id"); // Airport ID
                String airportName = rs.getString("name"); // Airport name
                String countryName = rs.getString("country_name"); // Country name
                int companyId = rs.getInt("company_id"); // Company ID

                // Add formatted string to dropdown
                airportDropdown.addItem(String.format("%s - %s (%s) [Company ID: %d]",
                        airportId, airportName, countryName, companyId));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching airports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showReadRecordDialog() {
        JDialog dialog = new JDialog(this, "Read Airports Records", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton filterButton = new JButton("Read Records via Filters");
        JButton inputButton = new JButton("Read Record via Input");
        JButton cancelButton = new JButton("Cancel");

        filterButton.setPreferredSize(new Dimension(200, 35));
        inputButton.setPreferredSize(new Dimension(200, 35));
        cancelButton.setPreferredSize(new Dimension(200, 35));

        filterButton.addActionListener(e -> {
            dialog.dispose();
            showFilterDialog();
        });

        inputButton.addActionListener(e -> {
            dialog.dispose();
            showReadInputDialog();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(filterButton);
        buttonPanel.add(inputButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showFilterDialog() {
        // Create dialog with smaller size
        JDialog dialog = new JDialog(this, "Read Records via Filters", true);
        dialog.setSize(600, 400);  // Smaller size for a cleaner look
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Create a panel for the selections
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control of layout
        GridBagConstraints gbc = new GridBagConstraints();
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set GridBagLayout constraints
        gbc.insets = new Insets(5, 10, 5, 10); // Adds padding between components
        gbc.anchor = GridBagConstraints.WEST; // Left-align components

        // Labels for information and order by
        JLabel includeLabel = new JLabel("<html><body>Select Airports Information to Include (min. 2):</body></html>");
        JLabel orderByLabel = new JLabel("Order By (max. 1):");

        // Positioning labels with GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        selectionPanel.add(includeLabel, gbc);
        gbc.gridx = 2; gbc.gridwidth = 2;
        selectionPanel.add(orderByLabel, gbc);

        // Info checkboxes
        JCheckBox airportIdCheckbox = new JCheckBox("Airports ID");
        JCheckBox airportNameCheckbox = new JCheckBox("Airports Name");
        JCheckBox countryNameCheckbox = new JCheckBox("Country Name");
        JCheckBox companyIdCheckbox = new JCheckBox("Company ID");

        // "All" checkbox placed below the other checkboxes
        JCheckBox allCheckbox = new JCheckBox("All");

        // Order by checkboxes (initially disabled)
        JCheckBox orderByAirportId = new JCheckBox("Airports ID");
        JCheckBox orderByAirportName = new JCheckBox("Airports Name");
        JCheckBox orderByCountryName = new JCheckBox("Country Name");
        JCheckBox orderByCompanyId = new JCheckBox("Company ID");

        // Initially disable order-by checkboxes
        orderByAirportId.setEnabled(false);
        orderByAirportName.setEnabled(false);
        orderByCountryName.setEnabled(false);
        orderByCompanyId.setEnabled(false);

        // Add info checkboxes to the panel (left-aligned)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        selectionPanel.add(airportIdCheckbox, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        selectionPanel.add(airportNameCheckbox, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        selectionPanel.add(countryNameCheckbox, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        selectionPanel.add(companyIdCheckbox, gbc);

        // Add order-by checkboxes to the panel (left-aligned)
        gbc.gridx = 2; gbc.gridy = 2;
        selectionPanel.add(orderByAirportId, gbc);
        gbc.gridx = 2; gbc.gridy = 3;
        selectionPanel.add(orderByAirportName, gbc);
        gbc.gridx = 2; gbc.gridy = 4;
        selectionPanel.add(orderByCountryName, gbc);
        gbc.gridx = 2; gbc.gridy = 5;
        selectionPanel.add(orderByCompanyId, gbc);

        // Add "All" checkbox below the other checkboxes
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        selectionPanel.add(allCheckbox, gbc);

        // Read and Cancel Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton readButton = new JButton("Read");
        JButton cancelButton = new JButton("Cancel");
        readButton.setEnabled(false);

        // Logic to enable/disable buttons and checkboxes
        ActionListener checkboxListener = e -> {
            int selectedInfoCount = (airportIdCheckbox.isSelected() ? 1 : 0) +
                    (airportNameCheckbox.isSelected() ? 1 : 0) +
                    (countryNameCheckbox.isSelected() ? 1 : 0) +
                    (companyIdCheckbox.isSelected() ? 1 : 0);

            int selectedOrderByCount = (orderByAirportId.isSelected() ? 1 : 0) +
                    (orderByAirportName.isSelected() ? 1 : 0) +
                    (orderByCountryName.isSelected() ? 1 : 0) +
                    (orderByCompanyId.isSelected() ? 1 : 0);

            // Enable/disable read button based on selection criteria
            readButton.setEnabled(selectedInfoCount >= 2 && selectedOrderByCount <= 1);

            // Enable order by checkboxes only if corresponding info checkbox is selected
            orderByAirportId.setEnabled(airportIdCheckbox.isSelected());
            orderByAirportName.setEnabled(airportNameCheckbox.isSelected());
            orderByCountryName.setEnabled(countryNameCheckbox.isSelected());
            orderByCompanyId.setEnabled(companyIdCheckbox.isSelected());

            // Untick order-by checkboxes if corresponding info checkbox is unticked
            if (!airportIdCheckbox.isSelected()) orderByAirportId.setSelected(false);
            if (!airportNameCheckbox.isSelected()) orderByAirportName.setSelected(false);
            if (!countryNameCheckbox.isSelected()) orderByCountryName.setSelected(false);
            if (!companyIdCheckbox.isSelected()) orderByCompanyId.setSelected(false);

            // Update "All" checkbox status based on the other checkboxes
            allCheckbox.setSelected(airportIdCheckbox.isSelected() &&
                    airportNameCheckbox.isSelected() &&
                    countryNameCheckbox.isSelected() &&
                    companyIdCheckbox.isSelected());
        };

        // Add listeners to all checkboxes
        airportIdCheckbox.addActionListener(checkboxListener);
        airportNameCheckbox.addActionListener(checkboxListener);
        countryNameCheckbox.addActionListener(checkboxListener);
        companyIdCheckbox.addActionListener(checkboxListener);
        orderByAirportId.addActionListener(checkboxListener);
        orderByAirportName.addActionListener(checkboxListener);
        orderByCountryName.addActionListener(checkboxListener);
        orderByCompanyId.addActionListener(checkboxListener);

        // All checkbox logic
        allCheckbox.addActionListener(e -> {
            boolean isSelected = allCheckbox.isSelected();
            airportIdCheckbox.setSelected(isSelected);
            airportNameCheckbox.setSelected(isSelected);
            countryNameCheckbox.setSelected(isSelected);
            companyIdCheckbox.setSelected(isSelected);

            // Disable other checkboxes when "All" is selected
            airportIdCheckbox.setEnabled(!isSelected);
            airportNameCheckbox.setEnabled(!isSelected);
            countryNameCheckbox.setEnabled(!isSelected);
            companyIdCheckbox.setEnabled(!isSelected);

            // Update the checkbox listener manually for all info checkboxes
            checkboxListener.actionPerformed(null);
        });

        // Read Button Logic
        readButton.addActionListener(e -> {
            List<String> columns = new ArrayList<>();

            // Check selected columns for airports info
            if (airportIdCheckbox.isSelected()) columns.add("airport_id");
            if (airportNameCheckbox.isSelected()) columns.add("name");
            if (countryNameCheckbox.isSelected()) columns.add("country_name");
            if (companyIdCheckbox.isSelected()) columns.add("company_id");

            // Constructing the SELECT query
            StringBuilder query = new StringBuilder("SELECT ");
            query.append(String.join(", ", columns)).append(" FROM airports");

            // Add ORDER BY clause if selected
            if (orderByAirportId.isSelected()) query.append(" ORDER BY airport_id");
            else if (orderByAirportName.isSelected()) query.append(" ORDER BY name");
            else if (orderByCountryName.isSelected()) query.append(" ORDER BY country_name");
            else if (orderByCompanyId.isSelected()) query.append(" ORDER BY company_id");

            try {
                // Pass query to manageRecord for execution
                List<Object[]> results = manageRecord.readWithQuery(query.toString());

                // Convert results into a table
                String[] columnNames = columns.toArray(new String[0]);
                Object[][] data = new Object[results.size()][columns.size()];

                for (int i = 0; i < results.size(); i++) {
                    Object[] row = results.get(i);
                    for (int j = 0; j < row.length; j++) {
                        data[i][j] = row[j];
                    }
                }

                // Create a JTable for displaying the results
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

        // Add the panels to the dialog
        dialog.add(selectionPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showReadInputDialog() {
        JDialog dialog = new JDialog(this, "Read Airports Record via Input", true);
        dialog.setSize(1000, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels and text fields
        JLabel airportIdLabel = new JLabel("Airport ID (Single or Range, e.g., 1 or 1-10):");
        JTextField airportIdField = new JTextField();

        JLabel airportNameLabel = new JLabel("Airport Name (Single or Comma-separated, e.g., JFK, LAX):");
        JTextField airportNameField = new JTextField();

        JLabel countryNameLabel = new JLabel("Country Name (Single or Comma-separated, e.g., UAE, United Kingdom):");
        JTextField countryNameField = new JTextField();

        JLabel companyIdLabel = new JLabel("Company ID (Single or Range, e.g., 5 or 5-15):");
        JTextField companyIdField = new JTextField();

        inputPanel.add(airportIdLabel);
        inputPanel.add(airportIdField);
        inputPanel.add(airportNameLabel);
        inputPanel.add(airportNameField);
        inputPanel.add(countryNameLabel);
        inputPanel.add(countryNameField);
        inputPanel.add(companyIdLabel);
        inputPanel.add(companyIdField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        searchButton.addActionListener(e -> {
            try {
                // Build WHERE clause based on input fields
                StringBuilder whereClause = new StringBuilder();

                // Parse Airport ID
                if (!airportIdField.getText().trim().isEmpty()) {
                    String airportIdInput = airportIdField.getText().trim();
                    if (airportIdInput.contains("-")) {
                        String[] range = airportIdInput.split("-");
                        whereClause.append("airport_id BETWEEN ")
                                .append(range[0].trim())
                                .append(" AND ")
                                .append(range[1].trim())
                                .append(" AND ");
                    } else {
                        whereClause.append("airport_id = ").append(airportIdInput).append(" AND ");
                    }
                }

                // Parse Airport Name
                if (!airportNameField.getText().trim().isEmpty()) {
                    String[] airportNames = airportNameField.getText().trim().split(",");
                    whereClause.append("(");
                    for (String name : airportNames) {
                        whereClause.append("name = '").append(name.trim()).append("' OR ");
                    }
                    whereClause.setLength(whereClause.length() - 4); // Remove the last " OR "
                    whereClause.append(") AND ");
                }

                // Parse Country Name
                if (!countryNameField.getText().trim().isEmpty()) {
                    String[] countryNames = countryNameField.getText().trim().split(",");
                    whereClause.append("(");
                    for (String country : countryNames) {
                        whereClause.append("country_name = '").append(country.trim()).append("' OR ");
                    }
                    whereClause.setLength(whereClause.length() - 4); // Remove the last " OR "
                    whereClause.append(") AND ");
                }

                // Parse Company ID
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

                // Remove the last " AND " if the clause exists
                if (whereClause.length() > 0) {
                    whereClause.setLength(whereClause.length() - 5);
                }

                // Construct the final query
                String query = whereClause.length() > 0 ? whereClause.toString() : null;
                List<Object[]> results;
                List<String> columnNames = List.of("Airport ID", "Name", "Country Name", "Company ID");

                if (query == null || query.isEmpty()) {
                    results = manageRecord.readWithQuery("SELECT * FROM airports");
                } else {
                    results = manageRecord.readWithQuery("SELECT * FROM airports WHERE " + query);
                }

                // Prepare data for JTable
                Object[][] data = new Object[results.size()][columnNames.size()];
                for (int i = 0; i < results.size(); i++) {
                    data[i] = results.get(i);
                }

                // Create JTable for displaying results
                JTable resultTable = new JTable(data, columnNames.toArray());
                JScrollPane scrollPane = new JScrollPane(resultTable);
                JOptionPane.showMessageDialog(dialog, scrollPane, "Query Results", JOptionPane.INFORMATION_MESSAGE);
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

    private void showUpdateDialog() {
        JDialog dialog = new JDialog(this, "Update Airport Record", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel airportLabel = new JLabel("Select Airport:");
        JComboBox<String> airportDropdown = new JComboBox<>();
        populateAirportDropdown(connection, airportDropdown);
        JLabel nameLabel = new JLabel("Create New Airport Name:");
        JTextField nameField = new JTextField();

        JLabel companyLabel = new JLabel("Select New Airport Company:");
        JComboBox<String> companyDropdown = new JComboBox<>();
        populateCompanyDropdown(companyDropdown);

        // Add components to the input panel
        inputPanel.add(airportLabel);
        inputPanel.add(airportDropdown);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(companyLabel);
        inputPanel.add(companyDropdown);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        updateButton.setEnabled(false);

        // Enable the update button if either field or dropdown changes
        DocumentListener fieldListener = new DocumentListener() {
            private void toggleUpdateButton() {
                boolean isNameFieldNotEmpty = !nameField.getText().trim().isEmpty();
                boolean isCompanySelected = companyDropdown.getSelectedItem() != null;
                updateButton.setEnabled(isNameFieldNotEmpty || isCompanySelected);
            }

            public void insertUpdate(DocumentEvent e) { toggleUpdateButton(); }
            public void removeUpdate(DocumentEvent e) { toggleUpdateButton(); }
            public void changedUpdate(DocumentEvent e) { toggleUpdateButton(); }
        };

        nameField.getDocument().addDocumentListener(fieldListener);
        companyDropdown.addActionListener(e -> {
            boolean isNameFieldNotEmpty = !nameField.getText().trim().isEmpty();
            boolean isCompanySelected = companyDropdown.getSelectedItem() != null;
            updateButton.setEnabled(isNameFieldNotEmpty || isCompanySelected);
        });

        updateButton.addActionListener(e -> {
            String selectedAirport = (String) airportDropdown.getSelectedItem();
            String newName = nameField.getText().trim();
            String selectedCompany = (String) companyDropdown.getSelectedItem();

            try {
                if (selectedAirport == null || !selectedAirport.contains(" - ")) {
                    throw new IllegalArgumentException("Invalid airport selected.");
                }
                int airportId = Integer.parseInt(selectedAirport.split(" - ")[0].trim());
                Integer companyId = null;

                if (selectedCompany != null && selectedCompany.contains(" - ")) {
                    companyId = Integer.parseInt(selectedCompany.split(" - ")[0].trim());
                }

                // Validation for input lengths
                if (!newName.isEmpty() && newName.length() > 25) {
                    throw new IllegalArgumentException("New airport name exceeds 25 characters.");
                }

                // Update the record
                String[] columns = {"name", "company_id"};
                Object[] values = {newName.isEmpty() ? null : newName, companyId};
                String condition = "airport_id = " + airportId;

                manageRecord.update("airports", condition, columns, values);

                JOptionPane.showMessageDialog(dialog, "Airport record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
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

    private void showDeleteRecordDialog() {
        JDialog dialog = new JDialog(this, "Delete Airport Record", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Panel for the table
        JPanel tablePanel = new JPanel(new BorderLayout());

        // SQL query to fetch airport data
        String query = "SELECT airport_id, name, country_name, company_id FROM airports";

        // Fetch airport records using the readWithQuery method
        List<Object[]> airportData;
        try {
            airportData = manageRecord.readWithQuery(query);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error fetching airport records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Columns for the table
        String[] columnNames = {"Airport ID", "Name", "Country", "Company"};

        // Convert List<Object[]> to a 2D array for the table data
        Object[][] data = new Object[airportData.size()][4];
        for (int i = 0; i < airportData.size(); i++) {
            data[i] = airportData.get(i);
        }

        // Create the table to display airport records
        JTable airportTable = new JTable(data, columnNames);
        airportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection
        JScrollPane tableScrollPane = new JScrollPane(airportTable);

        // Add the table to the tablePanel
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create the Delete and Cancel buttons
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        deleteButton.setEnabled(false); // Initially disabled, will be enabled when a row is selected

        // Enable delete button when a row is selected
        airportTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && airportTable.getSelectedRow() != -1) {
                deleteButton.setEnabled(true);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = airportTable.getSelectedRow();
            if (selectedRow != -1) {
                int airportId = (int) airportTable.getValueAt(selectedRow, 0);

                int confirmation = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete Airport ID: " + airportId + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        // Build the condition string to match the selected airport_id
                        String condition = "airport_id = " + airportId;
                        // Call delete method from manageRecord class with the condition
                        manageRecord.delete("airports", condition);
                        JOptionPane.showMessageDialog(dialog, "Airport deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Error deleting airport: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
