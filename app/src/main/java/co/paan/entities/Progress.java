package co.paan.entities;

/**
 * Created by remi on 12/10/15.
 */
public class Progress {
    private String lineRead;
    private String currentParseDate;
    private String startDate;

    public Progress() {
    }

    public Progress(Integer lineRead, String currentParseDate, String startDate) {
        this.lineRead = String.valueOf(lineRead);
        this.currentParseDate = currentParseDate;
        this.startDate = startDate;
    }

    public String getLineRead() {
        return lineRead;
    }

    public String getCurrentParseDate() {
        return currentParseDate;
    }

    public String getStartDate() {
        return startDate;
    }
}
