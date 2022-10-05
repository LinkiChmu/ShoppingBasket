import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ClientLog {
    private List<LogNote> activityLog;

    public ClientLog() {
        activityLog = new LinkedList<>();
    }

    /**
     * Adds title line to the ClientLog;
     */
    public void firstLog (String fileSaveLog) {
        String[] firstStr = {"productNum", "amount"};
        try(CSVWriter writer = new CSVWriter(new FileWriter(fileSaveLog, true))){
            writer.writeNext(firstStr, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a client action to the log;
     * display the note.
     */
    public void log(int productNum, int amount) {
        LogNote note = new LogNote(productNum, amount);
        activityLog.add(note);

        note.displayNote();
    }

    /**
     * Writes client log to the CSV file.
     */
    public void exportAsCSV(File txtFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile, true))) {
            activityLog.stream()
                    .map(LogNote::toString)
                    .map(str -> str.split(","))
                    .forEach(arr -> writer.writeNext(arr, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
