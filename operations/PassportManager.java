package operations;

import java.sql.Connection;
import java.sql.SQLException;

public class PassportManager {
    private ManageRecord manageRecord;

    public PassportManager(Connection connection) {
        this.manageRecord = new ManageRecord(connection);
    }
    
    public void addPassport(int passportId, String firstName, String middleName, String lastName, String nationality,
                            String issueDate, String expirationDate, String dateOfBirth, String sex, String placeOfIssue) throws SQLException {
        String table = "passports";
        String[] columns = {"passport_id", "first_name", "middle_name", "last_name", "date_of_birth", "sex", "nationality", 
                            "place_of_issue", "issue_date", "expiration_date"};
        Object[] values = {passportId, firstName, middleName, lastName, dateOfBirth, sex, nationality, placeOfIssue, issueDate, expirationDate};

        manageRecord.create(table, columns, values);
    }

    public String getAllPassports() throws SQLException {
        return manageRecord.readAll("passports");
    }

    public String getPassportById(int passportId) throws SQLException {
        String table = "passports";
        String condition = "passport_id = " + passportId;
        return manageRecord.readWithCondition(table, condition);
    }

    public String getPassportsByName(String firstName, String lastName) throws SQLException {
        String table = "passports";
        String condition = "first_name = '" + firstName + "' AND last_name = '" + lastName + "'";
        return manageRecord.readWithCondition(table, condition);
    }

    public String getPassportsByNationality(String nationality) throws SQLException {
        String table = "passports";
        String condition = "nationality = '" + nationality + "'";
        return manageRecord.readWithCondition(table, condition);
    }

    public String getPassportsBySex(String sex) throws SQLException {
        String table = "passports";
        String condition = "sex = '" + sex + "'";
        return manageRecord.readWithCondition(table, condition);
    }

    public void updatePassport(int passportId, String firstName, String middleName, String lastName, String nationality, 
                            String placeOfIssue, String issueDate, String expirationDate) throws SQLException {
        String table = "passports";
        String condition = "passport_id = " + passportId;

        // setting middle name to null because it is not a required field
        if (middleName == null || middleName.isEmpty()) {
            middleName = null;
        }

        String[] columns = {"first_name", "middle_name", "last_name", "nationality", "place_of_issue", "issue_date", "expiration_date"};
        Object[] values = {firstName, middleName, lastName, nationality, placeOfIssue, issueDate, expirationDate};

        manageRecord.update(table, condition, columns, values);
    }

    public void deletePassport(int passportId) throws SQLException {
        String table = "passports";
        String condition = "passport_id = " + passportId;

        manageRecord.delete(table, condition);
    }
}
