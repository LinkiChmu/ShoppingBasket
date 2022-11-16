import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ClientLog {
    protected List<Note> activityLog;

    public ClientLog() {
        activityLog = new LinkedList<>();
    }

    /**
     * Adds title line to the ClientLog;
     */
    public void firstLog(String fileSaveLog) throws IOException {
        String[] firstStr = {"productNum", "amount"};
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileSaveLog, true))) {
            writer.writeNext(firstStr, false);
        }
    }

    /**
     * Saves a client action to the log;
     * display the note.
     */
    public void log(int productNum, int amount) {
        Note note = new Note(productNum, amount);
        activityLog.add(note);

        note.displayNote();
    }

    /**
     * Writes client log to the CSV file.
     */
    public void exportAsCSV(File txtFile) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile, true))) {
            activityLog.stream()
                    .map(Note::toString)
                    .map(str -> str.split(","))
                    .forEach(arr -> writer.writeNext(arr, false));
        }
    }
}
