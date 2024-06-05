package rfidscanquery;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        String sheetId = "17TKyWMRgxCRlMNKiBuL4yK1KuRjs5vhsoSUD7aajfZ8";

        // Posting an attendance entry
        AttendanceManager.postAttendanceEntry(sheetId, "10027394", "2024-06-05", "3:10 PM");

        // Getting attendance entries for a student
        List<List<Object>> entries = AttendanceManager.getAttendanceEntries(sheetId, "10027394");
        System.out.println(entries);

        // Getting student info
        StudentInfoManager.StudentInfo studentInfo = StudentInfoManager.getStudentInfo(sheetId, "10027394");
        System.out.println(studentInfo);
    }
}
