package co.paan.rest.DTO;

/**
 * Created by remi on 09/09/2017.
 */
public class ReceivedMailInfo {
    private String originalConversationName;
    private String senderName;
    private String senderEmail;

    public ReceivedMailInfo(String originalConversationName, String senderName, String senderEmail) {
        this.originalConversationName = originalConversationName;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
    }

    public String getOriginalConversationName() {
        return originalConversationName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }
}
