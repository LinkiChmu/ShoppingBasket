import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ClientLog {

    private String name;
    private static List<LogNote> activityLog = new LinkedList<>();

    public ClientLog() {

    }

    /**
     * Saves a client action to add a purchase to the log;
     * display the note.
     */
    public static void log(int productNum, int amount) {
        LogNote note = new LogNote(productNum, amount);
        activityLog.add(note);

        note.displayNote();
    }

    /**
     * Writes client log to the CSV file.
     */
    public static void exportAsCSV(File txtFile) {
        String[] firstStr = {"productNum", "amount"};

        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile, true))) {

            writer.writeNext(firstStr, false);

            activityLog.stream()
                    .map(LogNote::toString)
                    .map(str -> str.split(","))
                    .forEach(arr -> writer.writeNext(arr, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
