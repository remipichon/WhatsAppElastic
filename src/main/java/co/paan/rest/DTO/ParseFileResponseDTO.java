package co.paan.rest.DTO;

import java.util.ArrayList;

/**
 * Created by remi on 04/07/15.
 */
public class ParseFileResponseDTO {

    int postCount;
    int lineCount;

    public ParseFileResponseDTO(int postCount, int lineCount) {
        this.postCount = postCount;
        this.lineCount = lineCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }
}
