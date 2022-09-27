import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ClientLog {

    private List<int[]> activityLog = new LinkedList<>();
    int[] note = new int[2];

    public ClientLog() {
    }

    public void log(int productNum, int amount) {
        note[0] = productNum;
        note[1] = amount;
        activityLog.add(note);

    }

    public void exportAsCSV(File file) {
        // TODO: 27/09/2022
    }
}
