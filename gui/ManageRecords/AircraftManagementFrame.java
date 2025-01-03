package gui.ManageRecords;

import gui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import operations.*;

public class AircraftManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public AircraftManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        JFrame frame = new JFrame("Aircraft Record Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize to full screen
        frame.setUndecorated(false); // Set true for no window borders
        frame.setIconImage(new ImageIcon("logo.png").getImage());

        // Background panel
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = ImageIO.read(new File("db bg.png")); // Replace with your aircraft-specific image
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

        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing around components

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("Aircraft Record Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel selectionMessage = new JLabel("Select an action", SwingConstants.CENTER);
        selectionMessage.setFont(new Font("Arial", Font.PLAIN, 14));
        selectionMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(selectionMessage);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JButton createButton = new JButton("Create Aircraft Record");
        JButton updateButton = new JButton("Update Aircraft Record");
        JButton readButton = new JButton("Read Aircraft Record");
        JButton deleteButton = new JButton("Delete Aircraft Record");
        JButton backButton = new JButton("Back");

        Dimension buttonSize = new Dimension(250, 40);

        createButton.setPreferredSize(buttonSize);
        createButton.setMaximumSize(buttonSize);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> showCreateRecordDialog());

        updateButton.setPreferredSize(buttonSize);
        updateButton.setMaximumSize(buttonSize);
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> showUpdateRecordDialog());

        readButton.setPreferredSize(buttonSize);
        readButton.setMaximumSize(buttonSize);
        readButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        readButton.addActionListener(e -> showReadRecordDialog());

        deleteButton.setPreferredSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.addActionListener(e -> showDeleteRecordDialog());

        backButton.setPreferredSize(new Dimension(75, 30));
        backButton.setMaximumSize(new Dimension(75, 30));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            frame.dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report);
        });

        contentPanel.add(createButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(updateButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(readButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(deleteButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(backButton);

        // Add content to background
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(contentPanel, gbc);

        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }

    private void showCreateRecordDialog() {
        JDialog dialog = new JDialog(this, "Create Aircraft Record", true);
        dialog.setSize(450, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10)); // Adjusted for 4 rows
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create components
        JLabel idLabel = new JLabel("Aircraft ID:");
        JTextField idField = new JTextField();
        idField.setToolTipText("Enter an 11-character Aircraft ID (ex. 'Z902')");
        JLabel modelLabel = new JLabel("Aircraft Model:");
        JTextField modelField = new JTextField();
        modelField.setToolTipText("Enter an 11-character Aircraft Model (ex. 'Zeus 902')");
        JLabel capacityLabel = new JLabel("Maximum Capacity:");
        JTextField capacityField = new JTextField();
        capacityField.setToolTipText("Enter the maximum capacity");;

        // Add components in the correct order
        inputPanel.add(idLabel); // Aircraft ID
        inputPanel.add(idField);
        inputPanel.add(modelLabel); // Aircraft Model
        inputPanel.add(modelField);
        inputPanel.add(capacityLabel); // Maximum Capacity
        inputPanel.add(capacityField);

        // Create buttons
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
            String aircraftId = idField.getText().trim().toUpperCase(); // Capitalize alphabetic characters
            String aircraftModel = modelField.getText().trim();
            String maxCapacityStr = capacityField.getText().trim();

            try {
                // Validate input
                if (aircraftId.length() > 11) {
                    throw new IllegalArgumentException("Aircraft ID cannot exceed 11 characters.");
                }
                if (aircraftModel.length() > 25) {
                    throw new IllegalArgumentException("Aircraft Model cannot exceed 25 characters.");
                }
                int maxCapacity = Integer.parseInt(maxCapacityStr);

                if (maxCapacity < 0 || maxCapacityStr.length() > 11) {
                    throw new IllegalArgumentException("Maximum Capacity must be a positive integer with at most 11 digits.");
                }

                // Check for duplicate aircraft model in the database
                if (isAircraftModelExists(aircraftModel)) {
                    throw new IllegalArgumentException("An aircraft with this model name already exists.");
                }

                // Create record in the database
                manageRecord.create("aircrafts", new String[]{"aircraft_id", "aircraft_model", "max_capacity"},
                        new Object[]{aircraftId, aircraftModel, maxCapacity});

                JOptionPane.showMessageDialog(dialog, "Aircraft record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Maximum Capacity must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(dialog, "Aircraft ID already exists. Please choose a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
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

    // Helper method to check for duplicate aircraft model names
    private boolean isAircraftModelExists(String aircraftModel) throws SQLException {
        String query = "SELECT COUNT(*) FROM aircrafts WHERE aircraft_model = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, aircraftModel);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if a record exists
                }
            }
        }
        return false;
    }


    private void populateAircraftDropdown(Connection connection, JComboBox<String> aircraftDropdown) {
        String query = "SELECT aircraft_id, aircraft_model, max_capacity FROM aircrafts";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear existing items in the dropdown
            aircraftDropdown.removeAllItems();

            // Populate the dropdown with the fetched results
            while (rs.next()) {
                String aircraftId = rs.getString("aircraft_id");
                String aircraftModel = rs.getString("aircraft_model");
                int maxCapacity = rs.getInt("max_capacity");

                // Add formatted string to dropdown
                aircraftDropdown.addItem(String.format("%s - %s (Capacity: %d)",
                        aircraftId, aircraftModel, maxCapacity));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching aircrafts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUpdateRecordDialog() {
        JDialog dialog = new JDialog(this, "Update Aircraft Record", true);
        dialog.setSize(600, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel aircraftLabel = new JLabel("Select Aircraft:");
        JComboBox<String> aircraftDropdown = new JComboBox<>();
        populateAircraftDropdown(connection, aircraftDropdown);

        JLabel nameLabel = new JLabel("Change Aircraft Name:");
        JTextField nameField = new JTextField();
        nameField.setToolTipText("Enter a 25-character Aircraft Name (ex. 'A503')");

        JLabel capacityLabel = new JLabel("Change Maximum Capacity:");
        JTextField capacityField = new JTextField();
        capacityField.setToolTipText("Enter the new maximum capacity");;

        // Add components to the input panel
        inputPanel.add(aircraftLabel);
        inputPanel.add(aircraftDropdown);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(capacityLabel);
        inputPanel.add(capacityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        updateButton.setEnabled(false);

        // Variables to hold current values
        final String[] currentAircraftModel = {null};
        final Integer[] currentMaxCapacity = {null};
        final String[] currentAircraftId = {null};

        // Populate current values when an aircraft is selected
        aircraftDropdown.addActionListener(e -> {
            String selectedAircraft = (String) aircraftDropdown.getSelectedItem();
            if (selectedAircraft != null && selectedAircraft.contains(" - ")) {
                String aircraftId = selectedAircraft.split(" - ")[0].trim();

                try (PreparedStatement stmt = connection.prepareStatement(
                        "SELECT aircraft_model, max_capacity FROM aircrafts WHERE aircraft_id = ?")) {
                    stmt.setString(1, aircraftId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            currentAircraftModel[0] = rs.getString("aircraft_model");
                            currentMaxCapacity[0] = rs.getInt("max_capacity");
                            currentAircraftId[0] = aircraftId;
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error fetching aircraft details: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                updateButton.setEnabled(true); // Enable the update button once an aircraft is selected
            }
        });

        updateButton.addActionListener(e -> {
            String selectedAircraft = (String) aircraftDropdown.getSelectedItem();
            String newName = nameField.getText().trim();
            String newCapacityStr = capacityField.getText().trim();

            try {
                if (selectedAircraft == null || !selectedAircraft.contains(" - ")) {
                    throw new IllegalArgumentException("Invalid aircraft selected.");
                }

                String aircraftId = selectedAircraft.split(" - ")[0].trim();
                Integer newCapacity = null;

                // Validate new capacity
                if (!newCapacityStr.isEmpty()) {
                    newCapacity = Integer.parseInt(newCapacityStr);
                    if (newCapacity <= 0 || newCapacityStr.length() > 11) {
                        throw new IllegalArgumentException("Maximum capacity must be a positive integer within 11 digits.");
                    }
                }

                // Validate new aircraft model name
                if (!newName.isEmpty() && newName.length() > 25) {
                    throw new IllegalArgumentException("Aircraft name exceeds 25 characters.");
                }

                // Check for duplicate aircraft model name only if the name is changing
                if (!newName.isEmpty() && isAircraftModelExists(newName)) {
                    throw new IllegalArgumentException("An aircraft with this name already exists.");
                }

                // Determine final values to update
                String finalName = newName.isEmpty() ? currentAircraftModel[0] : newName;
                Integer finalCapacity = newCapacity == null ? currentMaxCapacity[0] : newCapacity;

                // Update the record
                String[] columns = {"aircraft_model", "max_capacity"};
                Object[] values = {finalName, finalCapacity};
                String condition = "aircraft_id = '" + aircraftId + "'";

                manageRecord.update("aircrafts", condition, columns, values);

                JOptionPane.showMessageDialog(dialog, "Aircraft record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Maximum capacity must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void showReadRecordDialog() {
        JDialog dialog = new JDialog(this, "Read Aircraft Records", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton filterButton = new JButton("Use Filters");
        JButton inputButton = new JButton("Use Input");
        JButton cancelButton = new JButton("Cancel");

        filterButton.setPreferredSize(new Dimension(200, 35));
        inputButton.setPreferredSize(new Dimension(200, 35));
        cancelButton.setPreferredSize(new Dimension(75, 30));

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

    private void showReadInputDialog() {
        JDialog dialog = new JDialog(this, "Read Aircraft Record via Input", true);
        dialog.setSize(1000, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10)); // Increased rows for the new toggle
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels and text fields
        JLabel aircraftIdLabel = new JLabel("Aircraft ID:");
        JTextField aircraftIdField = new JTextField();
        aircraftIdField.setToolTipText("Enter in Single or Comma-separated, e.g., A350, B737");

        JLabel aircraftModelLabel = new JLabel("Aircraft Model:");
        JTextField aircraftModelField = new JTextField();
        aircraftModelField.setToolTipText("Enter in Single or Comma-separated, e.g., Airbus, Boeing");

        // Toggle for LIKE query
        JCheckBox likeQueryToggle = new JCheckBox("Enable Partial Matches");

        JLabel capacityLabel = new JLabel("Max Capacity:");
        JTextField capacityField = new JTextField();
        capacityField.setToolTipText("Enter in Single or Range, e.g., 150 or 150-300");

        inputPanel.add(aircraftIdLabel);
        inputPanel.add(aircraftIdField);
        inputPanel.add(aircraftModelLabel);
        inputPanel.add(aircraftModelField);
        inputPanel.add(new JLabel()); // Empty label for spacing
        inputPanel.add(likeQueryToggle); // Add toggle below the Aircraft Model field
        inputPanel.add(capacityLabel);
        inputPanel.add(capacityField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        searchButton.addActionListener(e -> {
            try {
                // Build WHERE clause based on input fields
                StringBuilder whereClause = new StringBuilder();

                // Parse Aircraft ID
                if (!aircraftIdField.getText().trim().isEmpty()) {
                    String[] aircraftIds = aircraftIdField.getText().trim().split(",");
                    whereClause.append("(");
                    for (String id : aircraftIds) {
                        whereClause.append("aircraft_id = '").append(id.trim()).append("' OR ");
                    }
                    whereClause.setLength(whereClause.length() - 4); // Remove the last " OR "
                    whereClause.append(") AND ");
                }

                // Parse Aircraft Model with optional LIKE query
                if (!aircraftModelField.getText().trim().isEmpty()) {
                    String[] aircraftModels = aircraftModelField.getText().trim().split(",");
                    whereClause.append("(");
                    for (String model : aircraftModels) {
                        if (likeQueryToggle.isSelected()) {
                            whereClause.append("aircraft_model LIKE '").append(model.trim()).append("' OR ");
                        } else {
                            whereClause.append("aircraft_model = '").append(model.trim()).append("' OR ");
                        }
                    }
                    whereClause.setLength(whereClause.length() - 4); // Remove the last " OR "
                    whereClause.append(") AND ");
                }

                // Parse Max Capacity
                if (!capacityField.getText().trim().isEmpty()) {
                    String capacityInput = capacityField.getText().trim();
                    if (capacityInput.contains("-")) {
                        String[] range = capacityInput.split("-");
                        whereClause.append("max_capacity BETWEEN ")
                                .append(range[0].trim())
                                .append(" AND ")
                                .append(range[1].trim())
                                .append(" AND ");
                    } else {
                        whereClause.append("max_capacity = ").append(capacityInput).append(" AND ");
                    }
                }

                // Remove the last " AND " if the clause exists
                if (whereClause.length() > 0) {
                    whereClause.setLength(whereClause.length() - 5);
                }

                // Construct the final query
                String query = whereClause.length() > 0 ? whereClause.toString() : null;
                List<Object[]> results;
                List<String> columnNames = List.of("Aircraft ID", "Aircraft Model", "Max Capacity");

                if (query == null || query.isEmpty()) {
                    results = manageRecord.readWithQuery("SELECT * FROM aircrafts");
                } else {
                    results = manageRecord.readWithQuery("SELECT * FROM aircrafts WHERE " + query);
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
        JDialog dialog = new JDialog(this, "Read Records via Filters", true);
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
        JLabel includeLabel = new JLabel("<html><body>Select Aircraft Information to Include (min. 2):</body></html>");
        JLabel orderByLabel = new JLabel("Order By (max. 1):");
        JLabel orderAdviceLabel = new JLabel("<html><body><i>Note: Default arrangement is ascending.</i></body></html>");

        // Positioning labels
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        selectionPanel.add(includeLabel, gbc);
        gbc.gridx = 2; gbc.gridwidth = 2;
        selectionPanel.add(orderByLabel, gbc);
        gbc.gridy = 4; gbc.gridwidth = 2;
        selectionPanel.add(orderAdviceLabel, gbc);

        // Info checkboxes
        JCheckBox aircraftIdCheckbox = new JCheckBox("Aircraft ID");
        JCheckBox aircraftModelCheckbox = new JCheckBox("Aircraft Model");
        JCheckBox maxCapacityCheckbox = new JCheckBox("Maximum Capacity");

        // "All" checkbox placed below the other checkboxes
        JCheckBox allCheckbox = new JCheckBox("All");

        // Order by checkboxes (initially disabled)
        JCheckBox orderByAircraftId = new JCheckBox("Aircraft ID");
        JCheckBox orderByAircraftModel = new JCheckBox("Aircraft Model");
        JCheckBox orderByMaxCapacity = new JCheckBox("Maximum Capacity");
        JCheckBox descendingOrderCheckbox = new JCheckBox("Descending Order"); // New checkbox

        // Initially disable order-by checkboxes
        orderByAircraftId.setEnabled(false);
        orderByAircraftModel.setEnabled(false);
        orderByMaxCapacity.setEnabled(false);
        descendingOrderCheckbox.setEnabled(false);

        // Add info checkboxes to the panel
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        selectionPanel.add(aircraftIdCheckbox, gbc);
        gbc.gridy = 2;
        selectionPanel.add(aircraftModelCheckbox, gbc);
        gbc.gridy = 3;
        selectionPanel.add(maxCapacityCheckbox, gbc);

        // Add order-by checkboxes to the panel
        gbc.gridx = 2; gbc.gridy = 1;
        selectionPanel.add(orderByAircraftId, gbc);
        gbc.gridy = 2;
        selectionPanel.add(orderByAircraftModel, gbc);
        gbc.gridy = 3;
        selectionPanel.add(orderByMaxCapacity, gbc);
        gbc.gridy = 5; // Positioning the descending order checkbox
        selectionPanel.add(descendingOrderCheckbox, gbc);

        // Add "All" checkbox
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        selectionPanel.add(allCheckbox, gbc);

        // Read and Cancel Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton readButton = new JButton("Read");
        JButton cancelButton = new JButton("Cancel");
        readButton.setEnabled(false);

        // Logic to enable/disable buttons and checkboxes
        ActionListener checkboxListener = e -> {
            int selectedInfoCount = (aircraftIdCheckbox.isSelected() ? 1 : 0) +
                    (aircraftModelCheckbox.isSelected() ? 1 : 0) +
                    (maxCapacityCheckbox.isSelected() ? 1 : 0);

            int selectedOrderByCount = (orderByAircraftId.isSelected() ? 1 : 0) +
                    (orderByAircraftModel.isSelected() ? 1 : 0) +
                    (orderByMaxCapacity.isSelected() ? 1 : 0);

            // Enable/disable the Read button based on selection criteria
            readButton.setEnabled(selectedInfoCount >= 2 && selectedOrderByCount == 1);

            // Enable order-by checkboxes only if corresponding info checkbox is selected
            orderByAircraftId.setEnabled(aircraftIdCheckbox.isSelected());
            orderByAircraftModel.setEnabled(aircraftModelCheckbox.isSelected());
            orderByMaxCapacity.setEnabled(maxCapacityCheckbox.isSelected());

            // Untick order-by checkboxes if corresponding info checkbox is unticked
            if (!aircraftIdCheckbox.isSelected()) orderByAircraftId.setSelected(false);
            if (!aircraftModelCheckbox.isSelected()) orderByAircraftModel.setSelected(false);
            if (!maxCapacityCheckbox.isSelected()) orderByMaxCapacity.setSelected(false);

            // Enable descending order checkbox if any order-by checkbox is selected
            descendingOrderCheckbox.setEnabled(selectedOrderByCount > 0);

            // Update "All" checkbox status based on the other checkboxes
            allCheckbox.setSelected(aircraftIdCheckbox.isSelected() &&
                    aircraftModelCheckbox.isSelected() &&
                    maxCapacityCheckbox.isSelected());
        };

        // Add listeners to all checkboxes
        aircraftIdCheckbox.addActionListener(checkboxListener);
        aircraftModelCheckbox.addActionListener(checkboxListener);
        maxCapacityCheckbox.addActionListener(checkboxListener);
        orderByAircraftId.addActionListener(checkboxListener);
        orderByAircraftModel.addActionListener(checkboxListener);
        orderByMaxCapacity.addActionListener(checkboxListener);

        // All checkbox logic
        allCheckbox.addActionListener(e -> {
            boolean isSelected = allCheckbox.isSelected();
            aircraftIdCheckbox.setSelected(isSelected);
            aircraftModelCheckbox.setSelected(isSelected);
            maxCapacityCheckbox.setSelected(isSelected);

            // Disable other checkboxes when "All" is selected
            aircraftIdCheckbox.setEnabled(!isSelected);
            aircraftModelCheckbox.setEnabled(!isSelected);
            maxCapacityCheckbox.setEnabled(!isSelected);

            // Update the checkbox listener manually for all info checkboxes
            checkboxListener.actionPerformed(null);
        });

        // Read Button Logic
        readButton.addActionListener(e -> {
            List<String> columns = new ArrayList<>();

            // Check selected columns for aircraft info
            if (aircraftIdCheckbox.isSelected()) columns.add("aircraft_id");
            if (aircraftModelCheckbox.isSelected()) columns.add("aircraft_model");
            if (maxCapacityCheckbox.isSelected()) columns.add("max_capacity");

            // Construct SELECT query
            StringBuilder query = new StringBuilder("SELECT ");
            query.append(String.join(", ", columns)).append(" FROM aircrafts");

            // Add ORDER BY clause if selected
            if (orderByAircraftId.isSelected()) query.append(" ORDER BY aircraft_id");
            else if (orderByAircraftModel.isSelected()) query.append(" ORDER BY aircraft_model");
            else if (orderByMaxCapacity.isSelected()) query.append(" ORDER BY max_capacity");

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
        JDialog dialog = new JDialog(this, "Delete Aircraft Record", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Panel for the table
        JPanel tablePanel = new JPanel(new BorderLayout());

        // SQL query to fetch aircraft data
        String query = "SELECT aircraft_id, aircraft_model, max_capacity FROM aircrafts";

        // Fetch aircraft records using the readWithQuery method
        List<Object[]> aircraftData;
        try {
            aircraftData = manageRecord.readWithQuery(query);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error fetching aircraft records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Columns for the table
        String[] columnNames = {"aircraft_id", "aircraft_model", "max_capacity"};

        // Convert List<Object[]> to a 2D array for the table data
        Object[][] data = new Object[aircraftData.size()][3];
        for (int i = 0; i < aircraftData.size(); i++) {
            data[i] = aircraftData.get(i);
        }

        // Create the table to display aircraft records
        JTable aircraftTable = new JTable(data, columnNames);
        aircraftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection
        JScrollPane tableScrollPane = new JScrollPane(aircraftTable);

        // Add the table to the tablePanel
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create the Delete and Cancel buttons
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        deleteButton.setEnabled(false); // Initially disabled, will be enabled when a row is selected

        // Enable delete button when a row is selected
        aircraftTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && aircraftTable.getSelectedRow() != -1) {
                deleteButton.setEnabled(true);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = aircraftTable.getSelectedRow();
            if (selectedRow != -1) {
                String aircraftId = (String) aircraftTable.getValueAt(selectedRow, 0);

                int confirmation = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete Aircraft ID: " + aircraftId + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        // Build the condition string to match the selected aircraft_id
                        String condition = "aircraft_id = '" + aircraftId + "'";
                        // Call delete method from manageRecord class with the condition
                        manageRecord.delete("aircrafts", condition);
                        JOptionPane.showMessageDialog(dialog, "Aircraft deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Error deleting aircraft: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

