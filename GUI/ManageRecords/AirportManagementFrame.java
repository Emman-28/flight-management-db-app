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
        // TODO: Add functionality for updating records

        JButton readButton = new JButton("Read Airports Record");
        readButton.setPreferredSize(buttonSize);
        readButton.addActionListener(e -> showReadRecordDialog());

        JButton deleteButton = new JButton("Delete Airports Record");
        deleteButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for deleting records

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
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Airports ID (11 chars):");
        JTextField idField = new JTextField();
        JLabel nameLabel = new JLabel("Name (25 chars):");
        JTextField nameField = new JTextField();
        JLabel countryLabel = new JLabel("Country (25 chars):");
        JTextField countryField = new JTextField();
        JLabel companyLabel = new JLabel("Select Company:");

        // Create company dropdown
        JComboBox<String> companyDropdown = new JComboBox<>();
        populateCompanyDropdown(companyDropdown); // Method to load companies from the database

        // Add components to the input panel
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(countryLabel);
        inputPanel.add(countryField);
        inputPanel.add(companyLabel);
        inputPanel.add(companyDropdown);

        // Add message below the dropdown
        JLabel infoLabel = new JLabel("<html><i>If desired company is missing from the options, proceed to Company Record Management.</i></html>");
        infoLabel.setForeground(Color.GRAY);
        inputPanel.add(new JLabel()); // Empty cell for alignment
        inputPanel.add(infoLabel);

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
                        companyDropdown.getSelectedItem() != null);
            }

            public void insertUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        idField.getDocument().addDocumentListener(fieldListener);
        nameField.getDocument().addDocumentListener(fieldListener);
        countryField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String airport_id = idField.getText().trim();
            String name = nameField.getText().trim();
            String country_name = countryField.getText().trim();
            String selectedCompany = (String) companyDropdown.getSelectedItem();

            try {
                if (selectedCompany == null || !selectedCompany.contains(" - ")) {
                    throw new IllegalArgumentException("Invalid company selected.");
                }
                int company_id = Integer.parseInt(selectedCompany.split(" - ")[0].trim());

                if (airport_id.length() > 11 || name.length() > 25 || country_name.length() > 25) {
                    throw new IllegalArgumentException("Input length exceeds allowed character limits.");
                }

                manageRecord.create("airports", new String[]{"airport_id", "name", "country_name", "company_id"},
                        new Object[]{airport_id, name, country_name, company_id});
                JOptionPane.showMessageDialog(dialog, "Record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(dialog, "Airports ID already exists. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
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

}
