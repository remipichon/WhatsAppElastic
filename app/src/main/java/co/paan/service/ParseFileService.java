package co.paan.service;

import co.paan.entities.Conversation;
import org.springframework.scheduling.annotation.Async;

import java.io.InputStream;

/**
 * Created by remi on 04/07/15.
 */
public interface ParseFileService {

    void parseFile(InputStream inputStream, String fileName, String conversationName);//, String webSocketId);

    @Async
    void parseFile(InputStream inputStream, Conversation conversation);

    String getWebSocketChannel(String conversationName);
}
