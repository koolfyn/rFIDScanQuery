package rfidscanquery;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManager {

    public static void postAttendanceEntry(String sheetId, String studentId, String date, String time) throws GeneralSecurityException, IOException {
        SheetsQuickStart quickStart = new SheetsQuickStart();
        Sheets service = quickStart.getService();

        String sheetRange = "Attendance!A:C"; // for the "Attendance" subsheet

        List<List<Object>> values = Arrays.asList(
                Arrays.asList(studentId, date, time)
        );
        ValueRange body = new ValueRange().setValues(values);

        service.spreadsheets().values().append(sheetId, sheetRange, body)
            .setValueInputOption("RAW")
            .execute();

        System.out.println("Attendance entry appended.");
        
    }

    public static List<List<Object>> getAttendanceEntries(String sheetId, String studentId) throws GeneralSecurityException, IOException {
        SheetsQuickStart quickStart = new SheetsQuickStart();
        Sheets service = quickStart.getService();

        String sheetRange = "Attendance!A:B"; // for the "Attendance" subsheet

        ValueRange response = service.spreadsheets().values()
            .get(sheetId, sheetRange)
            .execute();
        List<List<Object>> values = response.getValues();
        List<List<Object>> attendanceEntries = new ArrayList<>();

        if (values != null) {
            for (List<Object> row : values) {
                if (row.get(0).equals(studentId)) {
                    attendanceEntries.add(row);
                }
            }
        }

        return attendanceEntries;
    }
}
