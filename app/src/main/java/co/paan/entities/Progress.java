package co.paan.entities;

/**
 * Created by remi on 12/10/15.
 */
public class Progress {
    private Integer lineRead;
    private Integer lineCount;


    public Progress(Integer lineRead, Integer lineCount) {
        this.lineRead = lineRead;
        this.lineCount = lineCount;
    }

    public Integer getLineRead() {
        return lineRead;
    }

    public void setLineRead(Integer lineRead) {
        this.lineRead = lineRead;
    }

    public Integer getLineCount() {
        return lineCount;
    }

    public void setLineCount(Integer lineCount) {
        this.lineCount = lineCount;
    }
}
