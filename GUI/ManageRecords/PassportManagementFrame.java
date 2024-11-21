package GUI.ManageRecords;

import GUI.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import operations.*;

public class PassportManagementFrame extends JFrame {
    private final ManageRecord manageRecord;
    private final ExecuteTransaction transaction;
    private final GenerateReport report;
    private final Connection connection;

    public PassportManagementFrame(Connection connection, ManageRecord manageRecord, ExecuteTransaction transaction, GenerateReport report) {
        this.connection = connection;
        this.manageRecord = manageRecord;
        this.transaction = transaction;
        this.report = report;

        setTitle("Passport Record Management");
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
        JLabel selectionMessage = new JLabel("Select an action for the passport records:", SwingConstants.CENTER);
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

        JButton createButton = new JButton("Create Passport Record");
        createButton.setPreferredSize(buttonSize);
        createButton.addActionListener(e -> showCreateRecordDialog());

        JButton updateButton = new JButton("Update Passport Record");
        updateButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for updating records

        JButton readButton = new JButton("Read Passport Record");
        readButton.setPreferredSize(buttonSize);
        readButton.addActionListener(e -> showReadRecordDialog());

        JButton deleteButton = new JButton("Delete Passport Record");
        deleteButton.setPreferredSize(buttonSize);
        // TODO: Add functionality for deleting records

        JButton backButton = new JButton("Back to Records Menu");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            dispose();
            new ManageRecordsFrame(connection, manageRecord, transaction, report);
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
        JLabel firstNameLabel = new JLabel("First Name (25 chars):");
        JTextField firstNameField = new JTextField();
        JLabel middleNameLabel = new JLabel("Middle Name (25 chars):");
        JTextField middleNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name (25 chars):");
        JTextField lastNameField = new JTextField();

        // for the date fields
        String[] days = new String[31];
        String[] months = new String[12];
        String[] years = new String[100];
        fillDates(days, months, years);
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
            Object dateOfBirth = yearComboBoxBirth.getSelectedItem() + "-" + monthComboBoxBirth.getSelectedItem() + "-" + dayComboBoxBirth.getSelectedItem();
            Object issueDate = yearComboBoxIssue.getSelectedItem() + "-" + monthComboBoxIssue.getSelectedItem() + "-" + dayComboBoxIssue.getSelectedItem();
            Object expirationDate = yearComboBoxExp.getSelectedItem() + "-" + monthComboBoxExp.getSelectedItem() + "-" + dayComboBoxExp.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
        
            try {
                if(passport_id.length() > 11 || firstName.length() > 25 || middleName != null && middleName.length() > 25 || lastName.length() > 25 || nationality.length() > 25 || placeOfIssue.length() > 25) {
                    throw new IllegalArgumentException("Input length exceeds allowed character limits.");
                }
        
                if(dateOfBirth == null || issueDate == null || expirationDate == null || gender == null || passport_id == null || firstName == null || lastName == null || nationality == null || placeOfIssue == null) {
                    throw new IllegalArgumentException("All fields must be filled.");
                }

                if(nationality == "Select Nationality") {
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
                    new Object[]{passport_id, firstName, middleName, lastName, dateOfBirth, gender, nationality, placeOfIssue, issueDate, expirationDate});
        
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

    private void fillDates(String[] days, String[] months, String[] years) {
        for(int i = 0; i < 31; i++) {
            days[i] = String.format("%02d", i + 1);
        }
        for(int i = 0; i < 12; i++) {
            months[i] = String.format("%02d", i + 1); 
        }
        for(int i = 0; i < 100; i++) {
            years[i] = String.valueOf(2024 - i);
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
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

    }
}
