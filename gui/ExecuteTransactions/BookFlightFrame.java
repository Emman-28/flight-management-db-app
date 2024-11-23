package gui.ExecuteTransactions;

import gui.GenerateReports.PassengerTrafficFrame;
import gui.ManageRecords.*;
import operations.ExecuteTransaction;
import operations.GenerateReport;
import operations.ManageRecord;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static gui.ManageRecords.PassportManagementFrame.*;

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
        setSize(800, 600);
        setLocationRelativeTo(null); // centers window
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Step 1: Passenger Details
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        JLabel passportIdLabel = new JLabel("Passport ID:");
        JTextField passportIdField = new JTextField(10);
        JButton checkPassportButton = new JButton("Check Passport");

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passportIdLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passportIdField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(checkPassportButton, gbc);

        // Step 2: Flight Selection
        JLabel countryLabel = new JLabel("Select Destination Country:");
        JComboBox<String> countryDropdown = new JComboBox<>();
        JLabel flightLabel = new JLabel("Select Flight:");
        JComboBox<String> flightDropdown = new JComboBox<>();
        JButton loadFlightsButton = new JButton("Load Flights");

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(countryLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(countryDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(flightLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(flightDropdown, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        mainPanel.add(loadFlightsButton, gbc);

        // Step 3: Booking Confirmation
        JButton bookButton = new JButton("Book Flight");

        gbc.gridx = 1;
        gbc.gridy = 7;
        mainPanel.add(bookButton, gbc);

        // Event Handlers
        checkPassportButton.addActionListener(e -> {
            String passportId = passportIdField.getText();
            try {
                if (!passportExists(passportId)) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "Passport ID not found. Would you like to create a passport record?",
                            "Passport Not Found",
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        showCreateRecordDialog();
                    }
                } else if (!passengerExists(passportId)) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "Passenger record not found. Would you like to create a passenger record?",
                            "Passenger Not Found",
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        createPassengerRecord(firstNameField.getText(), lastNameField.getText(), passportId);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Passport and passenger records found. You can proceed to book a flight.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while checking records.");
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

        bookButton.addActionListener(e -> {
            String flightId = (String) flightDropdown.getSelectedItem();
            try {
                String seatNumber = generateSeatNumber(flightId);
                transaction.bookFlight(getPassengerId(passportIdField.getText()), flightId, seatNumber, new BigDecimal("5000.00")); // Example price
                JOptionPane.showMessageDialog(this, "Flight booked successfully. Your seat number is: " + seatNumber);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to book flight.");
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private boolean passportExists(String passportId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM passports WHERE passport_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, passportId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean passengerExists(String passportId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM passengers WHERE passport_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, passportId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void createPassengerRecord(String firstName, String lastName, String passportId) throws SQLException {
        String sql = "INSERT INTO passengers (first_name, last_name, passport_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, passportId);
            ps.executeUpdate();
        }
    }

    private void loadAvailableFlights(String country, JComboBox<String> flightDropdown) throws SQLException {
        String sql = "SELECT flight_id, airport, origin_country, destination_country, expected_departure_time, expected_arrival_time FROM flights WHERE destination_country = ? AND seating_capacity > 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, country);
            ResultSet rs = ps.executeQuery();
            flightDropdown.removeAllItems();
            while (rs.next()) {
                String flightDetails = String.format("%s - %s (%s to %s), Departure: %s, Arrival: %s",
                        rs.getString("flight_id"),
                        rs.getString("airport"),
                        rs.getString("origin_country"),
                        rs.getString("destination_country"),
                        rs.getTimestamp("expected_departure_time"),
                        rs.getTimestamp("expected_arrival_time"));
                flightDropdown.addItem(flightDetails);
            }
        }
    }

    private String generateSeatNumber(String flightId) throws SQLException {
        String sql = "SELECT seating_capacity FROM flights WHERE flight_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, flightId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int capacity = rs.getInt("seating_capacity");
                char row = (char) ('A' + (capacity / 3));
                int column = (capacity % 3) + 1;
                return row + String.valueOf(column);
            }
            throw new SQLException("Failed to retrieve flight seating capacity.");
        }
    }

    private int getPassengerId(String passportId) throws SQLException {
        String sql = "SELECT passenger_id FROM passengers WHERE passport_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, passportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("passenger_id");
            }
            throw new SQLException("Passenger ID not found.");
        }
    }

    public void showCreateRecordDialog() {
        JDialog dialog = new JDialog(this, "Create Passport Record", true);
        dialog.setSize(750, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // name fields
        JLabel idLabel = new JLabel("Passport ID:");
        JTextField idField = new JTextField();
        idField.setEditable(true);

        JLabel firstNameLabel = new JLabel("First Name (25 chars):");
        JTextField firstNameField = new JTextField();
        JLabel middleNameLabel = new JLabel("Middle Name (25 chars):");
        JTextField middleNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name (25 chars):");
        JTextField lastNameField = new JTextField();

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
        genderModel.addElement("Others");
        JLabel genderLabel = new JLabel("Gender:");
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

                if(dateOfBirth == null || issueDate == null || expirationDate == null || sex == "--" || passport_id == null || firstName == null || lastName == null || nationality == null || placeOfIssue == null) {
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
            years[i] = String.valueOf(3000 - i);
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
