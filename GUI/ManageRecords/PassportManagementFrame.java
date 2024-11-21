package GUI.ManageRecords;

import GUI.*;
import java.awt.*;
import java.sql.*;
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
        // readButton.addActionListener(e -> showReadRecordDialog());

        JButton deleteButton = new JButton("Delete Passport Record");
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
        JDialog dialog = new JDialog(this, "Create Passport Record", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Passport ID (11 chars):"); // #FIXME: should be int
        JTextField idField = new JTextField();
        JLabel FirstNameLabel = new JLabel("First Name (25 chars):");
        JTextField FirstNameField = new JTextField();
        JLabel middleNameLabel = new JLabel("Middle Name (25 chars):");
        JTextField middleNameField = new JTextField();
        JLabel LastNameLabel = new JLabel("Last Name (25 chars):");
        JTextField LastNameField = new JTextField();

        JLabel birthdateLabel = new JLabel("Date (YYYY-MM-DD):");
        SpinnerDateModel birthdateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(birthdateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        DefaultComboBoxModel<String> genderModel = new DefaultComboBoxModel<>();
        genderModel.addElement("Male");
        genderModel.addElement("Female");
        genderModel.addElement("Others");
        JLabel genderLabel = new JLabel("Gender:");
        JComboBox<String> customComboBox = new JComboBox<>(genderModel);

        JLabel nationalityLabel = new JLabel("Nationality (25 chars):");
        JTextField nationalityField = new JTextField();

        JLabel placeOfIssueLabel = new JLabel("Place of Issue (25 chars):");
        JTextField placeOfIssueField = new JTextField();

        JLabel issueDateLabel = new JLabel("Issue Date (YYYY-MM-DD):");
        SpinnerDateModel issueDateModel = new SpinnerDateModel();
        JSpinner issueDateSpinner = new JSpinner(issueDateModel);
        JSpinner.DateEditor issueDateEditor = new JSpinner.DateEditor(issueDateSpinner, "yyyy-MM-dd");
        issueDateSpinner.setEditor(issueDateEditor);

        JLabel expirationDateLabel = new JLabel("Expiration Date (YYYY-MM-DD):");
        SpinnerDateModel expirationDateModel = new SpinnerDateModel();
        JSpinner expirationDateSpinner = new JSpinner(expirationDateModel);
        JSpinner.DateEditor expirationDateEditor = new JSpinner.DateEditor(expirationDateSpinner, "yyyy-MM-dd");
        expirationDateSpinner.setEditor(expirationDateEditor);

        // Add components to the input panel
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(FirstNameLabel);
        inputPanel.add(FirstNameField);
        inputPanel.add(middleNameLabel);
        inputPanel.add(middleNameField);
        inputPanel.add(LastNameLabel);
        inputPanel.add(LastNameField);
        inputPanel.add(birthdateLabel);
        inputPanel.add(dateSpinner);
        inputPanel.add(genderLabel);
        inputPanel.add(customComboBox);
        inputPanel.add(nationalityLabel);
        inputPanel.add(nationalityField);
        inputPanel.add(placeOfIssueLabel);
        inputPanel.add(placeOfIssueField);
        inputPanel.add(issueDateLabel);
        inputPanel.add(issueDateSpinner);
        inputPanel.add(expirationDateLabel);
        inputPanel.add(expirationDateSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.setEnabled(false);

        // Enable the create button only if all fields are filled
        DocumentListener fieldListener = new DocumentListener() {
            private void checkFields() {
                createButton.setEnabled(
                    !idField.getText().trim().isEmpty() &&
                    !FirstNameField.getText().trim().isEmpty() &&
                    !LastNameField.getText().trim().isEmpty() &&
                    !nationalityField.getText().trim().isEmpty() &&
                    !placeOfIssueField.getText().trim().isEmpty() &&
                    dateSpinner.getValue() != null &&
                    issueDateSpinner.getValue() != null &&
                    expirationDateSpinner.getValue() != null &&
                    customComboBox.getSelectedItem() != null
                );
            }
        
            public void insertUpdate(DocumentEvent e) {
                checkFields(); // Call the checkFields() method
            }
        
            public void removeUpdate(DocumentEvent e) {
                checkFields(); // Call the checkFields() method
            }
        
            public void changedUpdate(DocumentEvent e) {
                checkFields(); // Call the checkFields() method
            }
        };

        // Add the fieldListener to all relevant fields
        idField.getDocument().addDocumentListener(fieldListener);
        FirstNameField.getDocument().addDocumentListener(fieldListener);
        LastNameField.getDocument().addDocumentListener(fieldListener);
        nationalityField.getDocument().addDocumentListener(fieldListener);
        placeOfIssueField.getDocument().addDocumentListener(fieldListener);

        createButton.addActionListener(e -> {
            String passport_id = idField.getText().trim();
            String firstName = FirstNameField.getText().trim();
            String middleName = middleNameField.getText().trim().isEmpty() ? null : middleNameField.getText().trim();
            String lastName = LastNameField.getText().trim();
            String nationality = nationalityField.getText().trim();
            String placeOfIssue = placeOfIssueField.getText().trim();
            Object dateOfBirth = dateSpinner.getValue();
            Object issueDate = issueDateSpinner.getValue();
            Object expirationDate = expirationDateSpinner.getValue();
            String gender = (String) customComboBox.getSelectedItem();
        
            try {
                if (passport_id.length() > 11 || firstName.length() > 25 || middleName != null && middleName.length() > 25 || lastName.length() > 25 || nationality.length() > 25 || placeOfIssue.length() > 25) {
                    throw new IllegalArgumentException("Input length exceeds allowed character limits.");
                }
        
                if (dateOfBirth == null || issueDate == null || expirationDate == null || gender == null) {
                    throw new IllegalArgumentException("All fields must be filled.");
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
}
