package gui.ManageRecords;

import gui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import operations.*;

public class PassportManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final Connection connection;
    private final GenerateReport report;

    public PassportManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        JFrame frame = new JFrame("Passport Record Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize to full screen
        frame.setUndecorated(false); // Set to true if you want no window borders
        frame.setIconImage(new ImageIcon("logo.png").getImage());
        
        // Setting background
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
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
        
        backgroundPanel.setLayout(new GridBagLayout()); // GridBagLayout centers content by default
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing around components
        
        // Panel to hold the content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false); // Transparent for background visibility
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Title section
        JLabel titleLabel = new JLabel("Passport Record Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between elements
        
        JLabel selectionMessage = new JLabel("Select an action", SwingConstants.CENTER);
        selectionMessage.setFont(new Font("Arial", Font.PLAIN, 14));
        selectionMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(selectionMessage);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between elements
        
        // Options section (Buttons)
        JButton createButton = new JButton("Create Passport Record");
        JButton updateButton = new JButton("Update Passport Record");
        JButton readButton = new JButton("Read Passport Record");
        JButton deleteButton = new JButton("Delete Passport Record");
        JButton backButton = new JButton("Back");
        
        Dimension buttonSize = new Dimension(250, 40);
        
        createButton.setPreferredSize(buttonSize);
        createButton.setMaximumSize(buttonSize);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> showCreateRecordDialog());
        
        updateButton.setPreferredSize(buttonSize);
        updateButton.setMaximumSize(buttonSize);
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> showUpdatePassportDialog());
        
        readButton.setPreferredSize(buttonSize);
        readButton.setMaximumSize(buttonSize);
        readButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        readButton.addActionListener(e -> showReadRecordDialog());
        
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.addActionListener(e -> showDeletePassportDialog());
        
        backButton.setPreferredSize(new Dimension(75, 30));
        backButton.setMaximumSize(new Dimension(75, 30));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            frame.dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report); // Back to Records Menu
        });
        
        contentPanel.add(createButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between buttons
        contentPanel.add(updateButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(readButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(deleteButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(backButton);
        
        // Add content panel to the center of the background
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Center in both X and Y axes
        backgroundPanel.add(contentPanel, gbc);
        
        // Add background to the frame
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
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
        idField.setToolTipText("Enter an 11-digit Passport ID");

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();
        firstNameField.setToolTipText("Enter a 25-character Name");
        JLabel middleNameLabel = new JLabel("Middle Name:");
        JTextField middleNameField = new JTextField();
        middleNameField.setToolTipText("Enter a 25-character Name");
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();
        lastNameField.setToolTipText("Enter a 25-character Name");

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
        JLabel birthdateLabel = new JLabel("Date:");

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
        JLabel placeOfIssueLabel = new JLabel("Place of Issue:");
        JTextField placeOfIssueField = new JTextField();
        placeOfIssueField.setToolTipText("Enter a 25-character Place of Issue");
        
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

    private void showReadRecordDialog() {
        JDialog dialog = new JDialog(this, "Read Passport Records", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton filterButton = new JButton("Use Filters");
        JButton inputButton = new JButton("Use Input");
        JButton cancelButton = new JButton("Cancel");;

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

    private void showFilterDialog() {
        JDialog dialog = new JDialog(this, "Filter Passport Records", true);
        dialog.setSize(600, 650); // Adjusted size for descending order feature
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
    
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // Set GridBagLayout constraints
        gbc.insets = new Insets(5, 10, 5, 10); // Adds padding between components
        gbc.anchor = GridBagConstraints.WEST; // Left-align components
    
        // Labels for information and order by
        JLabel includeLabel = new JLabel("<html><body>Select Passport Information to Include (min. 2):</body></html>");
        JLabel orderByLabel = new JLabel("Order By (max. 1):");
    
        // Positioning labels with GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        selectionPanel.add(includeLabel, gbc);
        gbc.gridx = 2; gbc.gridwidth = 2;
        selectionPanel.add(orderByLabel, gbc);
    
        // Info checkboxes
        JCheckBox passportIdCheckbox = new JCheckBox("Passport ID");
        JCheckBox firstNameCheckbox = new JCheckBox("First Name");
        JCheckBox middleNameCheckbox = new JCheckBox("Middle Name");
        JCheckBox lastNameCheckbox = new JCheckBox("Last Name");
        JCheckBox dateOfBirthCheckbox = new JCheckBox("Date of Birth");
        JCheckBox sexCheckbox = new JCheckBox("Sex");
        JCheckBox nationalityCheckbox = new JCheckBox("Nationality");
        JCheckBox placeOfIssueCheckbox = new JCheckBox("Place of Issue");
        JCheckBox issueDateCheckbox = new JCheckBox("Issue Date");
        JCheckBox expirationDateCheckbox = new JCheckBox("Expiration Date");
    
        // "All" checkbox placed below the other checkboxes
        JCheckBox allCheckbox = new JCheckBox("All");
    
        // Order by checkboxes (initially disabled)
        JCheckBox orderByPassportId = new JCheckBox("Passport ID");
        JCheckBox orderByFirstName = new JCheckBox("First Name");
        JCheckBox orderByMiddleName = new JCheckBox("Middle Name");
        JCheckBox orderByLastName = new JCheckBox("Last Name");
        JCheckBox orderByDateOfBirth = new JCheckBox("Date of Birth");
        JCheckBox orderBySex = new JCheckBox("Sex");
        JCheckBox orderByNationality = new JCheckBox("Nationality");
        JCheckBox orderByPlaceOfIssue = new JCheckBox("Place of Issue");
        JCheckBox orderByIssueDate = new JCheckBox("Issue Date");
        JCheckBox orderByExpirationDate = new JCheckBox("Expiration Date");
    
        JCheckBox descendingOrderCheckbox = new JCheckBox("Descending Order"); // Added descending order checkbox
    
        // Initially disable order-by checkboxes
        JCheckBox[] orderByCheckboxes = {
            orderByPassportId, orderByFirstName, orderByMiddleName, orderByLastName,
            orderByDateOfBirth, orderBySex, orderByNationality, orderByPlaceOfIssue,
            orderByIssueDate, orderByExpirationDate
        };
        for (JCheckBox checkBox : orderByCheckboxes) {
            checkBox.setEnabled(false);
        }
        descendingOrderCheckbox.setEnabled(false); // Initially disabled
    
        // Add info checkboxes to the panel
        JCheckBox[] infoCheckboxes = {
            passportIdCheckbox, firstNameCheckbox, middleNameCheckbox, lastNameCheckbox,
            dateOfBirthCheckbox, sexCheckbox, nationalityCheckbox, placeOfIssueCheckbox,
            issueDateCheckbox, expirationDateCheckbox
        };
    
        for (int i = 0; i < infoCheckboxes.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            selectionPanel.add(infoCheckboxes[i], gbc);
        }
    
        // Add order-by checkboxes to the panel
        for (int i = 0; i < orderByCheckboxes.length; i++) {
            gbc.gridx = 2;
            gbc.gridy = i + 1;
            selectionPanel.add(orderByCheckboxes[i], gbc);
        }
    
        // Add "All" checkbox
        gbc.gridx = 0; gbc.gridy = infoCheckboxes.length + 1; gbc.gridwidth = 1;
        selectionPanel.add(allCheckbox, gbc);
    
        // Add descending order checkbox below order-by checkboxes
        gbc.gridx = 2; gbc.gridy = infoCheckboxes.length + 2;
        selectionPanel.add(descendingOrderCheckbox, gbc);
    
        // Read and Cancel Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton readButton = new JButton("Read");
        JButton cancelButton = new JButton("Cancel");
        readButton.setEnabled(false);
    
        // Logic to enable/disable buttons and checkboxes
        ActionListener checkboxListener = e -> {
            int selectedInfoCount = 0;
            for (JCheckBox checkBox : infoCheckboxes) {
                if (checkBox.isSelected()) selectedInfoCount++;
            }
    
            int selectedOrderByCount = 0;
            for (JCheckBox checkBox : orderByCheckboxes) {
                if (checkBox.isSelected()) selectedOrderByCount++;
            }
    
            readButton.setEnabled(selectedInfoCount >= 2 && selectedOrderByCount <= 1);
    
            // Enable/disable order-by checkboxes based on info checkbox selection
            for (int i = 0; i < infoCheckboxes.length; i++) {
                orderByCheckboxes[i].setEnabled(infoCheckboxes[i].isSelected());
            }
    
            // Untick order-by checkboxes if corresponding info checkbox is unticked
            for (int i = 0; i < infoCheckboxes.length; i++) {
                if (!infoCheckboxes[i].isSelected()) {
                    orderByCheckboxes[i].setSelected(false);
                }
            }
    
            // Enable descending order checkbox only if one order-by checkbox is selected
            descendingOrderCheckbox.setEnabled(selectedOrderByCount == 1);
    
            // Update "All" checkbox status
            allCheckbox.setSelected(selectedInfoCount == infoCheckboxes.length);
        };
    
        // Add listeners to checkboxes
        for (JCheckBox checkBox : infoCheckboxes) {
            checkBox.addActionListener(checkboxListener);
        }
        for (JCheckBox checkBox : orderByCheckboxes) {
            checkBox.addActionListener(checkboxListener);
        }
    
        // "All" checkbox logic
        allCheckbox.addActionListener(e -> {
            boolean isSelected = allCheckbox.isSelected();
            for (JCheckBox checkBox : infoCheckboxes) {
                checkBox.setSelected(isSelected);
                checkBox.setEnabled(!isSelected);
            }
            checkboxListener.actionPerformed(null);
        });
    
        // Read Button Logic
        readButton.addActionListener(e -> {
            List<String> columns = new ArrayList<>();
            for (JCheckBox checkBox : infoCheckboxes) {
                if (checkBox.isSelected()) columns.add(checkBox.getText().toLowerCase().replace(" ", "_"));
            }
    
            StringBuilder query = new StringBuilder("SELECT ");
            query.append(String.join(", ", columns)).append(" FROM passports");
    
            for (JCheckBox checkBox : orderByCheckboxes) {
                if (checkBox.isSelected()) {
                    query.append(" ORDER BY ").append(checkBox.getText().toLowerCase().replace(" ", "_"));
                    break;
                }
            }
    
            if (descendingOrderCheckbox.isSelected()) {
                query.append(" DESC");
            }
    
            try {
                List<Object[]> results = manageRecord.readWithQuery(query.toString());
                String[] columnNames = columns.toArray(new String[0]);
                Object[][] data = new Object[results.size()][columns.size()];
    
                for (int i = 0; i < results.size(); i++) {
                    data[i] = results.get(i);
                }
    
                JTable resultTable = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(resultTable);
                JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE);
                JDialog messageDialog = optionPane.createDialog(dialog, "Query Results");
                messageDialog.setSize(1000, 500);
                messageDialog.setVisible(true);
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
        JDialog dialog = new JDialog(this, "Read Passport Records via Input", true);
        dialog.setSize(800, 700); // Adjusted size to fit all fields
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
    
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(14, 2, 10, 10)); // Increased grid size
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // Labels and text fields for passport attributes
        JLabel passportIdLabel = new JLabel("Passport ID:");
        JTextField passportIdField = new JTextField();
        passportIdField.setToolTipText("Enter in Single or Range, e.g., 1 or 1-10");

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();
        firstNameField.setToolTipText("Enter a 25-character Name");
        JCheckBox firstNameLikeToggle = new JCheckBox("Enable Partial Matches");

        JLabel middleNameLabel = new JLabel("Middle Name:");
        JTextField middleNameField = new JTextField();
        middleNameField.setToolTipText("Enter a 25-character Name");
        JCheckBox middleNameLikeToggle = new JCheckBox("Include Partial Matches");

        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();
        lastNameField.setToolTipText("Enter a 25-character Name");
        JCheckBox lastNameLikeToggle = new JCheckBox("Include Partial Matches");
    
        // Issue Date Fields
        String[] days = new String[32];
        String[] months = new String[13];
        String[] years = new String[101];
        fillDatesIssuance(days, months, years);

        JComboBox<String> dayComboBoxBirth = new JComboBox<>(days);
        JComboBox<String> monthComboBoxBirth = new JComboBox<>(months);
        JComboBox<String> yearComboBoxBirth = new JComboBox<>(years);

        dayComboBoxBirth.setPreferredSize(new Dimension(80, 20));
        monthComboBoxBirth.setPreferredSize(new Dimension(120, 20));
        yearComboBoxBirth.setPreferredSize(new Dimension(100, 20));

        JPanel birthDatePanel = new JPanel();
        birthDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        birthDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        birthDatePanel.add(monthComboBoxBirth);
        birthDatePanel.add(dayComboBoxBirth);
        birthDatePanel.add(yearComboBoxBirth);
        JLabel birthdateLabel = new JLabel("Birthdate:");
    
        JComboBox<String> dayComboBoxIssue = new JComboBox<>(days);
        JComboBox<String> monthComboBoxIssue = new JComboBox<>(months);
        JComboBox<String> yearComboBoxIssue = new JComboBox<>(years);
        
        dayComboBoxIssue.setPreferredSize(new Dimension(80, 20));
        monthComboBoxIssue.setPreferredSize(new Dimension(120, 20));
        yearComboBoxIssue.setPreferredSize(new Dimension(100, 20));

        JPanel issueDatePanel = new JPanel();
        issueDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        issueDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        issueDatePanel.add(monthComboBoxIssue);
        issueDatePanel.add(dayComboBoxIssue);
        issueDatePanel.add(yearComboBoxIssue);
        JLabel issueDateLabel = new JLabel("Issue Date:");
    
        // Expiration Date Fields
        JComboBox<String> dayComboBoxExpire = new JComboBox<>(days);
        JComboBox<String> monthComboBoxExpire = new JComboBox<>(months);
        JComboBox<String> yearComboBoxExpire = new JComboBox<>(years);
    
        dayComboBoxExpire.setPreferredSize(new Dimension(80, 20));
        monthComboBoxExpire.setPreferredSize(new Dimension(120, 20));
        yearComboBoxExpire.setPreferredSize(new Dimension(100, 20));
    
        JPanel expirationDatePanel = new JPanel();
        expirationDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        expirationDatePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        expirationDatePanel.add(monthComboBoxExpire);
        expirationDatePanel.add(dayComboBoxExpire);
        expirationDatePanel.add(yearComboBoxExpire);
        JLabel expirationDateLabel = new JLabel("Expiration Date:");
    
        DefaultComboBoxModel<String> genderModel = new DefaultComboBoxModel<>();
        genderModel.addElement("--");
        genderModel.addElement("Male");
        genderModel.addElement("Female");
        genderModel.addElement("Others");
        JLabel genderLabel = new JLabel("Gender:");
        JComboBox<String> genderComboBox = new JComboBox<>(genderModel);
    
        String[] nationalities = fillNationalities(); // Define this method to return nationality options
        JLabel nationalityLabel = new JLabel("Nationality:");
        JComboBox<String> nationalityComboBox = new JComboBox<>(nationalities);
    
        JLabel placeOfIssueLabel = new JLabel("Place of Issue:");
        JTextField placeOfIssueField = new JTextField();
        placeOfIssueField.setToolTipText("Enter a 25-character Place of Issue");
        JCheckBox placeOfIssueLikeToggle = new JCheckBox("Include Partial Matches");
    
        inputPanel.add(passportIdLabel);
        inputPanel.add(passportIdField);
        inputPanel.add(firstNameLabel);
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel());
        inputPanel.add(firstNameLikeToggle);
        inputPanel.add(middleNameLabel);
        inputPanel.add(middleNameField);
        inputPanel.add(new JLabel());
        inputPanel.add(middleNameLikeToggle);
        inputPanel.add(lastNameLabel);
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel());
        inputPanel.add(lastNameLikeToggle);
        inputPanel.add(birthdateLabel);
        inputPanel.add(birthDatePanel);
        inputPanel.add(genderLabel);
        inputPanel.add(genderComboBox);
        inputPanel.add(nationalityLabel);
        inputPanel.add(nationalityComboBox);
        inputPanel.add(placeOfIssueLabel);
        inputPanel.add(placeOfIssueField);
        inputPanel.add(new JLabel());
        inputPanel.add(placeOfIssueLikeToggle);
        inputPanel.add(issueDateLabel);
        inputPanel.add(issueDatePanel);
        inputPanel.add(expirationDateLabel);
        inputPanel.add(expirationDatePanel);
    
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");
    
        searchButton.addActionListener(e -> {
            try {
                StringBuilder whereClause = new StringBuilder();
        
                // Parse Passport ID
                if (!passportIdField.getText().trim().isEmpty()) {
                    String passportIdInput = passportIdField.getText().trim();
                    if (passportIdInput.contains("-")) {
                        String[] range = passportIdInput.split("-");
                        whereClause.append("passport_id BETWEEN ")
                                .append(range[0].trim())
                                .append(" AND ")
                                .append(range[1].trim())
                                .append(" AND ");
                    } else {
                        whereClause.append("passport_id = ").append(passportIdInput).append(" AND ");
                    }
                }
        
                // Parse First Name
                if (!firstNameField.getText().trim().isEmpty()) {
                    String firstNameInput = firstNameField.getText().trim();
                    if (firstNameLikeToggle.isSelected()) {
                        whereClause.append("first_name LIKE '%").append(firstNameInput).append("%' AND ");
                    } else {
                        whereClause.append("first_name = '").append(firstNameInput).append("' AND ");
                    }
                }
        
                // Parse Middle Name
                if (!middleNameField.getText().trim().isEmpty()) {
                    String middleNameInput = middleNameField.getText().trim();
                    if (middleNameLikeToggle.isSelected()) {
                        whereClause.append("middle_name LIKE '%").append(middleNameInput).append("%' AND ");
                    } else {
                        whereClause.append("middle_name = '").append(middleNameInput).append("' AND ");
                    }
                }
        
                // Parse Last Name
                if (!lastNameField.getText().trim().isEmpty()) {
                    String lastNameInput = lastNameField.getText().trim();
                    if (lastNameLikeToggle.isSelected()) {
                        whereClause.append("last_name LIKE '%").append(lastNameInput).append("%' AND ");
                    } else {
                        whereClause.append("last_name = '").append(lastNameInput).append("' AND ");
                    }
                }
        
                // Parse Birthdate
                String birthdate = yearComboBoxBirth.getSelectedItem() + "-" + monthComboBoxBirth.getSelectedItem() + "-" + dayComboBoxBirth.getSelectedItem();
                if (yearComboBoxBirth.getSelectedItem() != "--" && monthComboBoxBirth.getSelectedItem() != "--" && dayComboBoxBirth.getSelectedItem() != "--") {
                    whereClause.append("birthdate = '").append(birthdate).append("' AND ");
                }
        
                // Parse Gender
                if (genderComboBox.getSelectedItem() != "--") {
                    whereClause.append("sex = '").append(genderComboBox.getSelectedItem()).append("' AND ");
                }
        
                // Parse Nationality
                if (nationalityComboBox.getSelectedItem() != "Select Nationality") {
                    whereClause.append("nationality = '").append(nationalityComboBox.getSelectedItem()).append("' AND ");
                }
        
                // Parse Place of Issue
                if (!placeOfIssueField.getText().trim().isEmpty()) {
                    String placeOfIssueInput = placeOfIssueField.getText().trim();
                    if (placeOfIssueLikeToggle.isSelected()) {
                        whereClause.append("place_of_issue LIKE '%").append(placeOfIssueInput).append("%' AND ");
                    } else {
                        whereClause.append("place_of_issue = '").append(placeOfIssueInput).append("' AND ");
                    }
                }
        
                // Parse Issue Date
                String issueDate = yearComboBoxIssue.getSelectedItem() + "-" + monthComboBoxIssue.getSelectedItem() + "-" + dayComboBoxIssue.getSelectedItem();
                if (yearComboBoxIssue.getSelectedItem() != "--" && monthComboBoxIssue.getSelectedItem() != "--" && dayComboBoxIssue.getSelectedItem() != "--") {
                    whereClause.append("issue_date = '").append(issueDate).append("' AND ");
                }
        
                // Parse Expiration Date
                String expirationDate = yearComboBoxExpire.getSelectedItem() + "-" + monthComboBoxExpire.getSelectedItem() + "-" + dayComboBoxExpire.getSelectedItem();
                if (yearComboBoxExpire.getSelectedItem() != "--" && monthComboBoxExpire.getSelectedItem() != "--" && dayComboBoxExpire.getSelectedItem() != "--") {
                    whereClause.append("expiration_date = '").append(expirationDate).append("' AND ");
                }
        
                // Remove the last " AND " if the clause exists
                if (whereClause.length() > 0) {
                    whereClause.setLength(whereClause.length() - 5);
                }
        
                // Construct the final query
                String query = whereClause.length() > 0 ? whereClause.toString() : null;
                List<Object[]> results;
                List<String> columnNames = List.of("Passport ID", "First Name", "Middle Name", "Last Name", "Birthdate",
                                                    "Sex", "Nationality", "Place of Issue", "Issue Date", "Expiration Date");
        
                // Handle empty query condition
                if (query == null || query.isEmpty()) {
                    results = manageRecord.readWithQuery("SELECT * FROM passports"); // No filtering
                } else {
                    results = manageRecord.readWithQuery("SELECT * FROM passports WHERE " + query); // With filtering
                }
        
                // Prepare data for JTable
                Object[][] data = new Object[results.size()][columnNames.size()];
                for (int i = 0; i < results.size(); i++) {
                    data[i] = results.get(i);
                }
        
                // Create JTable to display results
                JTable resultTable = new JTable(data, columnNames.toArray());
                JScrollPane scrollPane = new JScrollPane(resultTable);
                JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE);
                JDialog messageDialog = optionPane.createDialog(dialog, "Query Results");
                messageDialog.setSize(1000, 500);
                messageDialog.setVisible(true);
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

    private void showUpdatePassportDialog() {
    JDialog dialog = new JDialog(this, "Update Passport Record", true);
    dialog.setSize(500, 350);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo(this);

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(5, 2, 10, 10));
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel passportLabel = new JLabel("Select Passport:");
    JComboBox<String> passportDropdown = new JComboBox<>();
    populatePassportDropdown(connection, passportDropdown); // Populate with passport records
    JLabel firstNameLabel = new JLabel("First Name:");
    JTextField firstNameField = new JTextField();
    firstNameField.setToolTipText("Enter a 25-character Name");

    JLabel middleNameLabel = new JLabel("Middle Name:");
    JTextField middleNameField = new JTextField();
    middleNameLabel.setToolTipText("Enter a 25-character Name");

    JLabel lastNameLabel = new JLabel("Last Name:");
    JTextField lastNameField = new JTextField();
    lastNameField.setToolTipText("Enter a 25-character Name");

    JLabel placeOfIssueLabel = new JLabel("Place of Issue:");
    JTextField placeOfIssueField = new JTextField();
    placeOfIssueField.setToolTipText("Enter a 25-character Place of Issue");

    // Add components to the input panel
    inputPanel.add(passportLabel);
    inputPanel.add(passportDropdown);
    inputPanel.add(firstNameLabel);
    inputPanel.add(firstNameField);
    inputPanel.add(middleNameLabel);
    inputPanel.add(middleNameField);
    inputPanel.add(lastNameLabel);
    inputPanel.add(lastNameField);
    inputPanel.add(placeOfIssueLabel);
    inputPanel.add(placeOfIssueField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton updateButton = new JButton("Update");
    JButton cancelButton = new JButton("Cancel");

    updateButton.setEnabled(false);

    // Enable the update button if any field changes
    DocumentListener fieldListener = new DocumentListener() {
        private void toggleUpdateButton() {
            boolean isFirstNameNotEmpty = !firstNameField.getText().trim().isEmpty();
            boolean isLastNameNotEmpty = !lastNameField.getText().trim().isEmpty();
            boolean isMiddleNameNotEmpty = !middleNameField.getText().trim().isEmpty();
            boolean isPlaceOfIssueNotEmpty = !placeOfIssueField.getText().trim().isEmpty();
            updateButton.setEnabled(isFirstNameNotEmpty || isLastNameNotEmpty || isMiddleNameNotEmpty || isPlaceOfIssueNotEmpty);
        }

        public void insertUpdate(DocumentEvent e) { toggleUpdateButton(); }
        public void removeUpdate(DocumentEvent e) { toggleUpdateButton(); }
        public void changedUpdate(DocumentEvent e) { toggleUpdateButton(); }
    };

    firstNameField.getDocument().addDocumentListener(fieldListener);
    lastNameField.getDocument().addDocumentListener(fieldListener);
    middleNameField.getDocument().addDocumentListener(fieldListener);
    placeOfIssueField.getDocument().addDocumentListener(fieldListener);

    updateButton.addActionListener(e -> {
        String selectedPassport = (String) passportDropdown.getSelectedItem();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String placeOfIssue = placeOfIssueField.getText().trim();

        try {
            if (selectedPassport == null || !selectedPassport.contains(" - ")) {
                throw new IllegalArgumentException("Invalid passport selected.");
            }
            int passportId = Integer.parseInt(selectedPassport.split(" - ")[0].trim());

            // Validation for input lengths
            if (firstName.length() > 25 || lastName.length() > 25 || middleName.length() > 25 || placeOfIssue.length() > 25) {
                throw new IllegalArgumentException("Input exceeds character limit of 25.");
            }

            // Update the record
            String[] columns = {"first_name", "last_name", "middle_name", "place_of_issue"};
            Object[] values = {firstName.isEmpty() ? null : firstName, 
                               lastName.isEmpty() ? null : lastName, 
                               middleName.isEmpty() ? null : middleName, 
                               placeOfIssue.isEmpty() ? null : placeOfIssue};
            String condition = "passport_id = " + passportId;

            manageRecord.update("passports", condition, columns, values);

            JOptionPane.showMessageDialog(dialog, "Passport record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    private void populatePassportDropdown(Connection connection, JComboBox<String> passportDropdown) {
        String query = "SELECT passport_id, first_name, last_name FROM passports";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Clear existing items in the dropdown
            passportDropdown.removeAllItems();
            
            // Populate the dropdown with passport records
            while (rs.next()) {
                int passportId = rs.getInt("passport_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                
                // Combine passport ID with first and last name
                String displayText = passportId + " - " + firstName + " " + lastName;
                
                // Add the formatted string to the dropdown
                passportDropdown.addItem(displayText);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching passport records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showDeletePassportDialog() {
        JDialog dialog = new JDialog(this, "Delete Passport Record", true);
        dialog.setSize(1000, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
    
        // Panel for the table
        JPanel tablePanel = new JPanel(new BorderLayout());
    
        // SQL query to fetch passport data
        String query = "SELECT passport_id, first_name, middle_name, last_name, date_of_birth, sex, nationality, place_of_issue, issue_date, expiration_date FROM passports";
    
        // Fetch passport records using the readWithQuery method
        List<Object[]> passportData;
        try {
            passportData = manageRecord.readWithQuery(query);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error fetching passport records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Columns for the table
        String[] columnNames = {"passport_id", "first_name", "middle_name", "last_name", "date_of_birth", "sex", "nationality", "place_of_issue", "issue_date", "expiration_date"};
    
        // Convert List<Object[]> to a 2D array for the table data
        Object[][] data = new Object[passportData.size()][10];
        for (int i = 0; i < passportData.size(); i++) {
            data[i] = passportData.get(i);
        }
    
        // Create the table to display passport records
        JTable passportTable = new JTable(data, columnNames);
        passportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection
        JScrollPane tableScrollPane = new JScrollPane(passportTable);
    
        // Add the table to the tablePanel
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
    
        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    
        // Create the Delete and Cancel buttons
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");
    
        deleteButton.setEnabled(false); // Initially disabled, will be enabled when a row is selected
    
        // Enable delete button when a row is selected
        passportTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && passportTable.getSelectedRow() != -1) {
                deleteButton.setEnabled(true);
            }
        });
    
        deleteButton.addActionListener(e -> {
            int selectedRow = passportTable.getSelectedRow();
            if (selectedRow != -1) {
                int passportId = (int) passportTable.getValueAt(selectedRow, 0);
    
                int confirmation = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to delete Passport ID: " + passportId + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        // Build the condition string to match the selected passport_id
                        String condition = "passport_id = " + passportId;
                        // Call delete method from manageRecord class with the condition
                        manageRecord.delete("passports", condition);
                        JOptionPane.showMessageDialog(dialog, "Passport deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Error deleting passport: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
