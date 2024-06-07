package rfidscanquery;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
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

        String range = "User!A:C"; // for the "User" subsheet

        ValueRange response = service.spreadsheets().values()
            .get(sheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();

        if (values != null) {
            for (List<Object> row : values) {
                if (row.get(2).equals(studentId)) { // Student ID is in the third column
                    String firstName = row.get(0).toString(); // First name is in the first column
                    String lastName = row.get(1).toString();  // Last name is in the second column
                    return new StudentInfo(firstName, lastName, studentId);
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

        public StudentInfo(String firstName, String lastName, String studentId) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.studentId = studentId;
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
        StudentInfo student = nfcIdMap.get(nfcId);

        if (student != null) {
            return student;
        } else {
            System.out.println("No match for NFC ID. Please enter your student ID:");
            String studentId = getUserInput();

            student = queryStudentDataByStudentId(studentId);
            if (student != null) {
                matchStudentIdToNfcId(studentId, nfcId);
                return student;
            } else {
                System.out.println("Invalid student ID. Please restart.");
                return null;
            }
        }
    }

    public StudentInfo queryStudentDataByStudentId(String studentId) {
        return studentIdMap.get(studentId);
    }

    public StudentInfo matchStudentIdToNfcId(String studentId, String nfcId) {
        StudentInfo student = studentIdMap.get(studentId);
        if (student != null) {
            student.setNfcId(nfcId);
            nfcIdMap.put(nfcId, student);
            System.out.println("NFC ID successfully matched to student ID.");
        } else {
            System.out.println("Failed to match NFC ID to student ID.");
        }
        return student;
    }

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    
}
