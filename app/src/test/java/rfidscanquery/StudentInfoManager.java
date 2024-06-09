package rfidscanquery;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StudentInfoManager {

    private Map<String, StudentInfo> studentIdMap = new HashMap<>();
    private Map<String, StudentInfo> nfcIdMap = new HashMap<>();

    public static StudentInfo getStudentInfo(String sheetId, String studentId) throws GeneralSecurityException, IOException {
        SheetsQuickStart quickStart = new SheetsQuickStart();
        Sheets service = quickStart.getService();

        String range = "User!A:D"; // for the "User" subsheet

        ValueRange response = service.spreadsheets().values()
            .get(sheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();

        if (values != null) {
            for (List<Object> row : values) {
                if (row.get(2).equals(studentId)) { // Student ID is in the third column
                    String firstName = row.get(0).toString(); // First name is in the first column
                    String lastName = row.get(1).toString();  // Last name is in the second column
                    String nfcID = row.get(3).toString(); // NFC ID in fourth column
                    return new StudentInfo(firstName, lastName, studentId, nfcID);
                }
            }
        }

        return null;
    }

    public static class StudentInfo {
        private String firstName;
        private String lastName;
        private String studentId;
        private String nfcId;

        public StudentInfo(String firstName, String lastName, String studentId, String nfcId) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.studentId = studentId;
            this.nfcId = nfcId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getNfcId() {
            return nfcId;
        }

        public void setNfcId(String nfcId) {
            this.nfcId = nfcId;
        }

        @Override
        public String toString() {
            return "StudentInfo{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", studentId='" + studentId + '\'' +
                    ", nfcId='" + nfcId + '\'' +
                    '}';
        }
    }

    public void addStudent(StudentInfo student) {
        studentIdMap.put(student.getStudentId(), student);
        if (student.getNfcId() != null) {
            nfcIdMap.put(student.getNfcId(), student);
        }
    }

    public StudentInfo getStudentInfoByNfcId(String nfcId) {
        return nfcIdMap.get(nfcId);
    }

    public StudentInfo queryStudentDataByStudentId(String studentId) {
        return studentIdMap.get(studentId);
    }

    public void matchStudentIdToNfcId(String studentId, String nfcId) {
        StudentInfo student = studentIdMap.get(studentId);
        if (student != null) {
            student.setNfcId(nfcId);
            nfcIdMap.put(nfcId, student);
            System.out.println("NFC ID successfully matched to student ID.");
        } else {
            System.out.println("Failed to match NFC ID to student ID.");
        }
    }

    public void getInfoByNFCId(String sheetId, String sheetRange, String nfcId) throws GeneralSecurityException, IOException {
        StudentInfo student = getStudentInfoFromSheet(sheetId, sheetRange, null, nfcId);
        if (student != null) {
            addStudent(student);
            System.out.println("Student Info: " + student);
        } else {
            System.out.println("No match found for NFC ID.");
        }
    }

    public void bindStudentIdToNfcId(String sheetId, String studentId, String nfcId) throws GeneralSecurityException, IOException {
        SheetsQuickStart quickStart = new SheetsQuickStart();
        Sheets service = quickStart.getService();

        String range = "User!A:D";
        ValueRange response = service.spreadsheets().values()
            .get(sheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (row.get(2).equals(studentId)) { // Student ID is in the third column
                    row.set(3, nfcId); // Set the NFC ID in the fourth column

                    ValueRange body = new ValueRange().setValues(values);
                    UpdateValuesResponse updateResponse = service.spreadsheets().values()
                        .update(sheetId, range, body)
                        .setValueInputOption("RAW")
                        .execute();

                    StudentInfo student = new StudentInfo(row.get(0).toString(), row.get(1).toString(), studentId, nfcId);
                    addStudent(student);

                    System.out.println("Student ID " + studentId + " successfully binded to NFC ID " + nfcId);
                    return;
                }
            }
        }
        System.out.println("Student ID not found in query.");
    }

    private StudentInfo getStudentInfoFromSheet(String sheetId, String sheetRange, String studentId, String nfcId) throws GeneralSecurityException, IOException {
        SheetsQuickStart quickStart = new SheetsQuickStart();
        Sheets service = quickStart.getService();

        ValueRange response = service.spreadsheets().values()
            .get(sheetId, sheetRange)
            .execute();
        List<List<Object>> values = response.getValues();

        if (values != null) {
            for (List<Object> row : values) {
                boolean idMatch = studentId != null && row.get(2).equals(studentId);
                boolean nfcMatch = nfcId != null && row.get(3).equals(nfcId);
                if (idMatch || nfcMatch) {
                    String firstName = row.get(0).toString();
                    String lastName = row.get(1).toString();
                    String studentIdValue = row.get(2).toString();
                    String nfcIdValue = row.get(3).toString();
                    return new StudentInfo(firstName, lastName, studentIdValue, nfcIdValue);
                }
            }
        }
        return null;
    }

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
