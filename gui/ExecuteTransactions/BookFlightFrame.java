package gui.ExecuteTransactions;

import operations.ExecuteTransaction;
import operations.GenerateReport;
import operations.ManageRecord;


import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Integer.parseInt;

public class BookFlightFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final Connection connection;
    private final GenerateReport report;

    public BookFlightFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        // frame settings
        setTitle("Book a Flight");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with a background image
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = ImageIO.read(new File("db bg.png"));
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
        mainPanel.setOpaque(false);

        // Create a content panel that will be centered and sized relative to the window
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        // Make the content panel semi-transparent with a white background
        contentPanel.setBackground(new Color(255, 255, 255, 200));

        GridBagConstraints panelGbc = new GridBagConstraints();
        panelGbc.gridx = 0;
        panelGbc.gridy = 0;
        panelGbc.weightx = 0.8; // Use 80% of the window width
        panelGbc.weighty = 0.9; // Use 90% of the window height
        panelGbc.fill = GridBagConstraints.BOTH;
        panelGbc.anchor = GridBagConstraints.CENTER;

        // Add padding around the content panel
        panelGbc.insets = new Insets(50, 50, 50, 50);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding between elements
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0; // Allow components to expand horizontally

        // Customize font sizes based on screen resolution
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int fontSize = screenHeight / 60; // Adjust font size based on screen height
        Font labelFont = new Font("Arial", Font.BOLD, fontSize);
        Font fieldFont = new Font("Arial", Font.PLAIN, fontSize);

        // Step 1: Passenger Details with scaled components
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        JLabel passportIdLabel = new JLabel("Passport ID:");
        JTextField passportIdField = new JTextField(10);
        ((AbstractDocument) passportIdField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String newString = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (text.matches("\\d*") && newString.length() <= 11) {  // Only allow digits and max 11 characters
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        JButton checkPassportButton = new JButton("Check Passport");

        // Apply fonts and sizes to components
        Component[] components = {
                firstNameLabel, firstNameField, lastNameLabel, lastNameField,
                passportIdLabel, passportIdField, checkPassportButton
        };

        for (Component comp : components) {
            if (comp instanceof JLabel) {
                comp.setFont(labelFont);
            } else {
                comp.setFont(fieldFont);
            }
            if (comp instanceof JTextField) {
                ((JTextField) comp).setPreferredSize(new Dimension(0, fontSize * 2));
            }
            if (comp instanceof JButton) {
                ((JButton) comp).setPreferredSize(new Dimension(0, fontSize * 3));
            }
        }

        // Layout components with proper scaling
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Label takes 30% of the width
        contentPanel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7; // Field takes 70% of the width
        contentPanel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        contentPanel.add(lastNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        contentPanel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        contentPanel.add(passportIdLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        contentPanel.add(passportIdField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.7;
        contentPanel.add(checkPassportButton, gbc);

        // Step 2: Flight Selection with scaled components
        JLabel countryLabel = new JLabel("Select Destination Country:");
        JComboBox<String> countryDropdown = new JComboBox<>();
        try {
            loadAvailableCountries(countryDropdown);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        JLabel flightLabel = new JLabel("Select Flight:");
        JComboBox<String> flightDropdown = new JComboBox<>();
        JButton loadFlightsButton = new JButton("Load Flights");

        // Apply fonts and sizes to flight selection components
        Component[] flightComponents = {
                countryLabel, countryDropdown, flightLabel, flightDropdown, loadFlightsButton
        };

        for (Component comp : flightComponents) {
            if (comp instanceof JLabel) {
                comp.setFont(labelFont);
            } else {
                comp.setFont(fieldFont);
            }
            if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setPreferredSize(new Dimension(0, fontSize * 2));
            }
            if (comp instanceof JButton) {
                ((JButton) comp).setPreferredSize(new Dimension(0, fontSize * 3));
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        contentPanel.add(countryLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        contentPanel.add(countryDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        contentPanel.add(flightLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        contentPanel.add(flightDropdown, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.7;
        contentPanel.add(loadFlightsButton, gbc);

        // Step 3: Booking Confirmation with scaled button
        JButton bookButton = new JButton("Book Flight");
        bookButton.setFont(fieldFont);
        bookButton.setPreferredSize(new Dimension(0, fontSize * 3));

        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weightx = 0.7;
        contentPanel.add(bookButton, gbc);

        // Add the content panel to the main panel
        mainPanel.add(contentPanel, panelGbc);

        // Event Handlers (unchanged)
        checkPassportButton.addActionListener(e -> {
            String passportIdText = passportIdField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            try {
                // Validate passport ID format
                if (passportIdText.isEmpty()) {
                    throw new IllegalArgumentException("Passport ID cannot be empty.");
                }
                if (passportIdText.length() > 11) {
                    throw new IllegalArgumentException("Passport ID cannot contain more than 11 digits.");
                }
                if (!passportIdText.matches("[0-9]+")) {
                    throw new IllegalArgumentException("Passport ID must contain only digits.");
                }

                int passportId = Integer.parseInt(passportIdText);

                if (!passportExists(passportId) || !passengerExists(passportId)) {
                    if (!passportExists(passportId)){
                        int response = JOptionPane.showConfirmDialog(this,
                                "Passport ID not found. Would you like to create a passport record?",
                                "Passport Not Found",
                                JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            showCreateRecordDialog(passportId, firstName, lastName);
                        }
                    }

                    if (!passengerExists(passportId)) {
                        int response = JOptionPane.showConfirmDialog(this,
                                "Passenger record not found. Would you like to create a passenger record?",
                                "Passenger Not Found",
                                JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            showCreatePassengerDialog(passportId);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Passport and passenger records found. You can proceed to book a flight.");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "An error occurred while checking records: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        loadFlightsButton.addActionListener(e -> {
            String selectedCountry = (String) countryDropdown.getSelectedItem();
            try {
                loadAvailableFlights(selectedCountry, flightDropdown);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load available flights.");
            }
        });

        // Step 4: Seat Selection
        JLabel rowLabel = new JLabel("Select Row:");
        JComboBox<String> rowDropdown = new JComboBox<>();
        JLabel seatLabel = new JLabel("Select Seat:");
        JComboBox<Integer> seatDropdown = new JComboBox<>();
        JButton loadSeatsButton = new JButton("Load Seats");

// Apply fonts and sizes to seat selection components
        Component[] seatComponents = {rowLabel, rowDropdown, seatLabel, seatDropdown, loadSeatsButton};
        for (Component comp : seatComponents) {
            if (comp instanceof JLabel) {
                comp.setFont(labelFont);
            } else {
                comp.setFont(fieldFont);
            }
            if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setPreferredSize(new Dimension(0, fontSize * 2));
            }
            if (comp instanceof JButton) {
                ((JButton) comp).setPreferredSize(new Dimension(0, fontSize * 3));
            }
        }

// Add seat selection components to the panel
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.3;
        contentPanel.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        contentPanel.add(rowDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.3;
        contentPanel.add(seatLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        contentPanel.add(seatDropdown, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 0.7;
        contentPanel.add(loadSeatsButton, gbc);

        loadSeatsButton.addActionListener(e -> {
            // Get the selected flight ID from the dropdown
            String selectedFlightId = (String) flightDropdown.getSelectedItem();

            // Check if the selected flight ID is not null and has at least 4 characters
            if (selectedFlightId != null && selectedFlightId.length() >= 4) {
                // Parse the flight ID to only keep the first 4 characters
                String flightId = selectedFlightId.substring(0, 4);  // Get the first 4 characters
                System.out.println("Parsed Flight ID: " + flightId);  // Debug statement

                // Proceed with removing items and loading seats
                rowDropdown.removeAllItems();
                seatDropdown.removeAllItems();

                try {
                    // Query for taken seats for the selected flight
                    Set<String> takenSeats = getTakenSeats(flightId);  // Use the parsed flightId
                    populateSeatOptions(rowDropdown, seatDropdown, takenSeats);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to load seat availability.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid flight before loading seats.");
            }
        });


        // Get the selected flight item from the dropdown
        String selectedFlightItem = (String) flightDropdown.getSelectedItem();

// Ensure the selected item is not null and contains the expected delimiter " — "
        if (selectedFlightItem != null && selectedFlightItem.contains(" — ")) {
            // Split the string by " — " and extract the first part as the flight ID
            String flightId = selectedFlightItem.split(" — ")[0];  // Get the first part before " — "
            System.out.println(flightId);
        }
// Event handler for booking the flight
        bookButton.addActionListener(e -> {
            // Get the selected flight ID from the dropdown
            String flightId = (String) ((String) flightDropdown.getSelectedItem()).substring(0,4);

            if (flightId == null || flightId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a flight before booking.", "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get other necessary details and proceed to book
            String selectedRow = (String) rowDropdown.getSelectedItem();
            Integer selectedSeat = (Integer) seatDropdown.getSelectedItem();

            if (selectedRow == null || selectedSeat == null) {
                JOptionPane.showMessageDialog(this, "Please select a seat before booking.", "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String seatNumber = selectedRow + selectedSeat;  // Combine row and seat into a single seat number (e.g., "A1")

            try {
                // Book the flight using the selected flight ID and seat
                transaction.bookFlight(getPassengerIdFromPassportId(parseInt(passportIdField.getText())), flightId, seatNumber, BigDecimal.valueOf(100.00));  // Example price
                JOptionPane.showMessageDialog(this, "Flight booked successfully. Your seat number is: " + seatNumber);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to book flight: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    // Helper Methods
    private Set<String> getTakenSeats(String flightId) throws SQLException {
        Set<String> takenSeats = new HashSet<>();
        String query = "SELECT seat_number FROM tickets WHERE booking_id IN " +
                "(SELECT booking_id FROM bookings WHERE flight_id = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, flightId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                takenSeats.add(rs.getString("seat_number"));
            }
        }
        return takenSeats;
    }

    private void populateSeatOptions(JComboBox<String> rowDropdown, JComboBox<Integer> seatDropdown, Set<String> takenSeats) {
        // Generate rows A-ZZ
        List<String> rows = new ArrayList<>();
        for (char c1 = 'A'; c1 <= 'Z'; c1++) {
            rows.add(String.valueOf(c1));
        }
        for (char c1 = 'A'; c1 <= 'Z'; c1++) {
            for (char c2 = 'A'; c2 <= 'Z'; c2++) {
                rows.add("" + c1 + c2);
            }
        }

        // Populate row dropdown
        for (String row : rows) {
            boolean rowHasFreeSeats = false;
            for (int seat = 1; seat <= 12; seat++) {
                String seatCode = row + seat;
                if (!takenSeats.contains(seatCode)) {
                    rowHasFreeSeats = true;
                }
            }
            if (rowHasFreeSeats) {
                rowDropdown.addItem(row);
            }
        }

        // Populate seat dropdown when row is selected
        rowDropdown.addActionListener(e -> {
            seatDropdown.removeAllItems();
            String selectedRow = (String) rowDropdown.getSelectedItem();
            if (selectedRow != null) {
                for (int seat = 1; seat <= 12; seat++) {
                    String seatCode = selectedRow + seat;
                    if (!takenSeats.contains(seatCode)) {
                        seatDropdown.addItem(seat);
                    }
                }
            }
        });
    }

    private boolean passportExists(int passportId) throws SQLException {
        return transaction.ensurePassportExists(passportId);
    }

    private boolean passengerExists(int passportId) throws SQLException {
        return transaction.doesPassengerExistWithPassport(passportId);
    }

    private void showCreatePassengerDialog(int passportId) {
        JDialog dialog = new JDialog(this, "Create Passenger Record", true);
        dialog.setSize(450, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create components
        JLabel contactLabel = new JLabel("Contact Number (20 digits):");
        JTextField contactField = new JTextField();
        JLabel emailLabel = new JLabel("Email Address (255 chars):");
        JTextField emailField = new JTextField();

        // Add components
        inputPanel.add(contactLabel);
        inputPanel.add(contactField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);

        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(!contactField.getText().trim().isEmpty() &&
                        !emailField.getText().trim().isEmpty());
            }

            public void insertUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        contactField.getDocument().addDocumentListener(fieldListener);
        emailField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String contactNumber = contactField.getText().trim();
            String emailAddress = emailField.getText().trim();

            try {
                // Validate input
                if (contactNumber.length() > 20) {
                    throw new IllegalArgumentException("Contact number cannot exceed 20 digits.");
                }
                if (emailAddress.length() > 255) {
                    throw new IllegalArgumentException("Email address cannot exceed 255 characters.");
                }

                // Validate email format
                if (!emailAddress.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new IllegalArgumentException("Please enter a valid email address.");
                }

                int nextPassengerId = getPassengerId(); // You'd need to implement this

                manageRecord.create("passengers",
                        new String[]{"passenger_id", "passport_id", "contact_number", "email_address"},
                        new Object[]{nextPassengerId, passportId, contactNumber, emailAddress});
                JOptionPane.showMessageDialog(dialog,
                        "Passenger record successfully created!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error creating record: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadAvailableCountries(JComboBox<String> countryDropdown) throws SQLException {
        String sql = """
        SELECT DISTINCT dest.country_name 
        FROM flights f
        JOIN airports dest ON f.dest_airport_id = dest.airport_id
        WHERE f.flight_status = 'SCHEDULED' 
        AND f.expected_departure_time > NOW()
        ORDER BY dest.country_name
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            countryDropdown.removeAllItems();
            countryDropdown.addItem("Select Country");
            while (rs.next()) {
                countryDropdown.addItem(rs.getString("country_name"));
            }
        }
    }

    private void loadAvailableFlights(String selectedCountry, JComboBox<String> flightDropdown) throws SQLException {
        String sql = """
        SELECT 
            f.flight_id,
            orig.country_name AS origin_country,
            dest.country_name AS destination_country,
            f.expected_departure_time,
            f.expected_arrival_time
        FROM flights f
        JOIN airports orig ON f.origin_airport_id = orig.airport_id
        JOIN airports dest ON f.dest_airport_id = dest.airport_id
        WHERE dest.country_name = ?
        AND f.flight_status = 'SCHEDULED'
        AND f.expected_departure_time > NOW()
        AND f.seating_capacity > (
            SELECT COUNT(*) 
            FROM bookings b 
            WHERE b.flight_id = f.flight_id 
            AND b.booking_status = 'CONFIRMED'
        )
        ORDER BY f.expected_departure_time
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, selectedCountry);
            ResultSet rs = ps.executeQuery();
            flightDropdown.removeAllItems();
            flightDropdown.addItem("Select Flight");

            while (rs.next()) {
                String flightDetails = String.format("%s — %s to %s — %s to %s",
                        rs.getString("flight_id"),
                        rs.getString("origin_country"),
                        rs.getString("destination_country"),
                        rs.getTimestamp("expected_departure_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        rs.getTimestamp("expected_arrival_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
                flightDropdown.addItem(flightDetails);
            }
        }
    }

    private int getPassengerId() throws SQLException {
        String sql = "SELECT MAX(passenger_id) FROM passengers";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1; // Returns the next available ID (highest + 1)
            }
            return 1; // If no records exist, start with ID 1
        }
    }

    public void showCreateRecordDialog(int prefillPassportId, String prefillFirstName, String prefillLastName) {
        JDialog dialog = new JDialog(this, "Create Passport Record", true);
        dialog.setSize(750, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // name fields
        JLabel idLabel = new JLabel("Passport ID:");
        JTextField idField = new JTextField(String.valueOf(prefillPassportId));
        idField.setEditable(true);

        JLabel firstNameLabel = new JLabel("First Name (25 chars):");
        JTextField firstNameField = new JTextField(prefillFirstName); // Pre-fill first name
        JLabel middleNameLabel = new JLabel("Middle Name (25 chars):");
        JTextField middleNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name (25 chars):");
        JTextField lastNameField = new JTextField(prefillLastName); // Pre-fill last name


        // for the date fields
        String[] days = new String[32];
        String[] months = new String[13];
        String[] years = new String[101];
        fillDatesIssuance(days, months, years);
        JComboBox<String> dayComboBoxBirth = new JComboBox<>(days);
        JComboBox<String> monthComboBoxBirth = new JComboBox<>(months);
        JComboBox<String> yearComboBoxBirth = new JComboBox<>(years);

        // birthdate fields
        dayComboBoxBirth.setPreferredSize(new Dimension(80, 30));
        monthComboBoxBirth.setPreferredSize(new Dimension(120, 30));
        yearComboBoxBirth.setPreferredSize(new Dimension(100, 30));
        JPanel birthDatePanel = new JPanel();
        birthDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        birthDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        birthDatePanel.add(monthComboBoxBirth);
        birthDatePanel.add(dayComboBoxBirth);
        birthDatePanel.add(yearComboBoxBirth);
        JLabel birthdateLabel = new JLabel("Date Of Birth:");

        // gender / sex
        DefaultComboBoxModel<String> genderModel = new DefaultComboBoxModel<>();
        genderModel.addElement("--");
        genderModel.addElement("Male");
        genderModel.addElement("Female");
        genderModel.addElement("Other");
        JLabel genderLabel = new JLabel("Sex:");
        JComboBox<String> genderComboBox = new JComboBox<>(genderModel);

        // nationalities
        String[] nationalities = fillNationalities();
        JLabel nationalityLabel = new JLabel("Nationality:");
        JComboBox<String> nationalityComboBox = new JComboBox<>(nationalities);

        // place of issue
        JLabel placeOfIssueLabel = new JLabel("Place of Issue (25 chars):");
        JTextField placeOfIssueField = new JTextField();

        // issue date
        fillDatesIssuance(days, months, years);
        JComboBox<String> dayComboBoxIssue = new JComboBox<>(days);
        JComboBox<String> monthComboBoxIssue = new JComboBox<>(months);
        JComboBox<String> yearComboBoxIssue = new JComboBox<>(years);

        JPanel issueDatePanel = new JPanel();
        issueDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        issueDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        issueDatePanel.add(monthComboBoxIssue);
        issueDatePanel.add(dayComboBoxIssue);
        issueDatePanel.add(yearComboBoxIssue);

        dayComboBoxIssue.setPreferredSize(new Dimension(80, 30));
        monthComboBoxIssue.setPreferredSize(new Dimension(120, 30));
        yearComboBoxIssue.setPreferredSize(new Dimension(100, 30));

        JLabel issueDateLabel = new JLabel("Issue Date:");

        // expiration date
        fillDatesExpiry(days, months, years);
        JComboBox<String> dayComboBoxExp = new JComboBox<>(days);
        JComboBox<String> monthComboBoxExp = new JComboBox<>(months);
        JComboBox<String> yearComboBoxExp = new JComboBox<>(years);

        dayComboBoxExp.setPreferredSize(new Dimension(80, 30));
        monthComboBoxExp.setPreferredSize(new Dimension(120, 30));
        yearComboBoxExp.setPreferredSize(new Dimension(100, 30));

        JPanel expDatePanel = new JPanel();
        expDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        expDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        expDatePanel.add(monthComboBoxExp);
        expDatePanel.add(dayComboBoxExp);
        expDatePanel.add(yearComboBoxExp);

        JLabel expirationDateLabel = new JLabel("Expiration Date:");

        // Add components to the input panel
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(firstNameLabel);
        inputPanel.add(firstNameField);
        inputPanel.add(middleNameLabel);
        inputPanel.add(middleNameField);
        inputPanel.add(lastNameLabel);
        inputPanel.add(lastNameField);
        inputPanel.add(birthdateLabel);
        inputPanel.add(birthDatePanel);
        inputPanel.add(genderLabel);
        inputPanel.add(genderComboBox);
        inputPanel.add(nationalityLabel);
        inputPanel.add(nationalityComboBox);
        inputPanel.add(placeOfIssueLabel);
        inputPanel.add(placeOfIssueField);
        inputPanel.add(issueDateLabel);
        inputPanel.add(issueDatePanel);
        inputPanel.add(expirationDateLabel);
        inputPanel.add(expDatePanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable the create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(
                        !idField.getText().trim().isEmpty() &&
                                !firstNameField.getText().trim().isEmpty() &&
                                !lastNameField.getText().trim().isEmpty() &&
                                nationalityComboBox.getSelectedItem()!= null &&
                                !placeOfIssueField.getText().trim().isEmpty() &&
                                dayComboBoxBirth.getSelectedItem() != null &&
                                monthComboBoxBirth.getSelectedItem()!= null &&
                                yearComboBoxBirth.getSelectedItem()!= null &&
                                dayComboBoxIssue.getSelectedItem()!= null &&
                                monthComboBoxIssue.getSelectedItem()!= null &&
                                yearComboBoxIssue.getSelectedItem()!= null &&
                                dayComboBoxExp.getSelectedItem()!= null &&
                                monthComboBoxExp.getSelectedItem()!= null &&
                                yearComboBoxExp.getSelectedItem()!= null
                );
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFields();
            }
        };

        idField.getDocument().addDocumentListener(fieldListener);
        firstNameField.getDocument().addDocumentListener(fieldListener);
        lastNameField.getDocument().addDocumentListener(fieldListener);
        placeOfIssueField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String passport_id = idField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim().isEmpty() ? null : middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String nationality = nationalityComboBox.getSelectedItem().toString();
            String placeOfIssue = placeOfIssueField.getText().trim();
            String sex = (String) genderComboBox.getSelectedItem();

            Object dateOfBirth;
            Object issueDate;
            Object expirationDate;
            if(yearComboBoxBirth.getSelectedItem() == "--" || monthComboBoxBirth.getSelectedItem() == "--" || dayComboBoxBirth.getSelectedItem() == "--") {
                dateOfBirth = null;
            } else {
                dateOfBirth = yearComboBoxBirth.getSelectedItem() + "-" + monthComboBoxBirth.getSelectedItem() + "-" + dayComboBoxBirth.getSelectedItem();
            }
            if(yearComboBoxIssue.getSelectedItem() == "--" || monthComboBoxIssue.getSelectedItem() == "--" || dayComboBoxIssue.getSelectedItem() == "--") {
                issueDate = null;
            } else {
                issueDate = yearComboBoxIssue.getSelectedItem() + "-" + monthComboBoxIssue.getSelectedItem() + "-" + dayComboBoxIssue.getSelectedItem();
            }
            if(yearComboBoxExp.getSelectedItem() == "--" || monthComboBoxExp.getSelectedItem() == "--" || dayComboBoxExp.getSelectedItem() == "--") {
                expirationDate = null;
            } else {
                expirationDate = yearComboBoxExp.getSelectedItem() + "-" + monthComboBoxExp.getSelectedItem() + "-" + dayComboBoxExp.getSelectedItem();
            }

            try {
                if(passport_id.length() > 11 || firstName.length() > 25 || middleName != null && middleName.length() > 25 || lastName.length() > 25 || nationality.length() > 25 || placeOfIssue.length() > 25) {
                    throw new IllegalArgumentException("Input length exceeds allowed character limits.");
                }

                if(dateOfBirth == null || issueDate == null || expirationDate == null || sex.equals("--") || passport_id == null || firstName == null || lastName == null || nationality == null || placeOfIssue == null) {
                    throw new IllegalArgumentException("All fields must be filled.");
                }

                if("Select Nationality".equals(nationality)) {
                    throw new IllegalArgumentException("Select a Nationality.");
                }

                String issueDateString = yearComboBoxIssue.getSelectedItem().toString() + "-"
                        + monthComboBoxIssue.getSelectedItem().toString() + "-"
                        + dayComboBoxIssue.getSelectedItem().toString();

                String expirationDateString = yearComboBoxExp.getSelectedItem().toString() + "-"
                        + monthComboBoxExp.getSelectedItem().toString() + "-"
                        + dayComboBoxExp.getSelectedItem().toString();

                LocalDate issueDateParsed = LocalDate.parse(issueDateString);
                LocalDate expirationDateParsed = LocalDate.parse(expirationDateString);

                if (!issueDateParsed.isBefore(expirationDateParsed)) {
                    throw new IllegalArgumentException("Issue date must be earlier than expiration date.");
                }

                manageRecord.create("passports",
                        new String[]{"passport_id", "first_name", "middle_name", "last_name", "date_of_birth", "sex", "nationality", "place_of_issue", "issue_date", "expiration_date"},
                        new Object[]{passport_id, firstName, middleName, lastName, dateOfBirth, sex, nationality, placeOfIssue, issueDate, expirationDate});

                JOptionPane.showMessageDialog(dialog, "Record successfully created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(dialog, "Passport ID already exists. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void fillDatesIssuance(String[] days, String[] months, String[] years) {
        days[0] = "--";
        for(int i = 1; i < 32; i++) {
            days[i] = String.format("%02d", i);
        }
        months[0] = "--";
        for(int i = 1; i < 13; i++) {
            months[i] = String.format("%02d", i);
        }
        years[0] = "--";
        for(int i = 1; i < 101; i++) {
            years[i] = String.valueOf(2025 - i);
        }
    }

    private void fillDatesExpiry(String[] days, String[] months, String[] years) {
        days[0] = "--";
        for(int i = 1; i < 32; i++) {
            days[i] = String.format("%02d", i);
        }
        months[0] = "--";
        for(int i = 1; i < 13; i++) {
            months[i] = String.format("%02d", i);
        }
        years[0] = "--";
        for(int i = 1; i < 101; i++) {
            years[i] = String.valueOf(2100 - i);
        }
    }

    private Integer getPassengerIdFromPassportId(int passportId) throws SQLException {
        // SQL to check if the passenger exists and fetch their ID
        String query = "SELECT passenger_id FROM passengers WHERE passport_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, passportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("passenger_id");
                } else {
                    // If no matching record found, return null
                    return null;
                }
            }
        }
    }


    private String[] fillNationalities() {
        String[] nationalities = {
                "Select Nationality",
                "Afghan",
                "Albanian",
                "Algerian",
                "American",
                "Argentinian",
                "Armenian",
                "Australian",
                "Austrian",
                "Bangladeshi",
                "Belgian",
                "Bolivian",
                "Brazilian",
                "British",
                "Bulgarian",
                "Cambodian",
                "Cameroonian",
                "Canadian",
                "Chilean",
                "Chinese",
                "Colombian",
                "Costa Rican",
                "Croatian",
                "Cuban",
                "Czech",
                "Danish",
                "Dominican",
                "Dutch",
                "Ecuadorian",
                "Egyptian",
                "Emirati",
                "English",
                "Estonian",
                "Ethiopian",
                "Filipino",
                "Finnish",
                "French",
                "Georgian",
                "German",
                "Ghanaian",
                "Greek",
                "Guatemalan",
                "Haitian",
                "Honduran",
                "Hungarian",
                "Icelandic",
                "Indian",
                "Indonesian",
                "Iranian",
                "Iraqi",
                "Irish",
                "Israeli",
                "Italian",
                "Ivorian",
                "Jamaican",
                "Japanese",
                "Jordanian",
                "Kazakh",
                "Kenyan",
                "Korean",
                "Kuwaiti",
                "Latvian",
                "Lebanese",
                "Liberian",
                "Libyan",
                "Lithuanian",
                "Malaysian",
                "Malian",
                "Mexican",
                "Moroccan",
                "Mozambican",
                "Namibian",
                "Nepalese",
                "New Zealander",
                "Nicaraguan",
                "Nigerian",
                "Norwegian",
                "Pakistani",
                "Palestinian",
                "Panamanian",
                "Paraguayan",
                "Peruvian",
                "Polish",
                "Portuguese",
                "Qatari",
                "Romanian",
                "Russian",
                "Saudi",
                "Scottish",
                "Senegalese",
                "Serbian",
                "Singaporean",
                "Slovak",
                "Slovenian",
                "Somali",
                "South African",
                "Spanish",
                "Sri Lankan",
                "Sudanese",
                "Swedish",
                "Swiss",
                "Syrian",
                "Taiwanese",
                "Tajik",
                "Tanzanian",
                "Thai",
                "Tunisian",
                "Turkish",
                "Ugandan",
                "Ukrainian",
                "Uruguayan",
                "Uzbek",
                "Venezuelan",
                "Vietnamese",
                "Welsh",
                "Zambian",
                "Zimbabwean",
                "Other"
        };

        return nationalities;
    }
}
