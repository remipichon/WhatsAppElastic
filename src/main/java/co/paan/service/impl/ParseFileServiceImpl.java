package co.paan.service.impl;

import co.paan.configuration.Channels;
import co.paan.entities.Post;
import co.paan.entities.Progress;
import co.paan.repository.ConversationRepository;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.PostService;
import com.google.common.io.CharStreams;
import org.elasticsearch.bootstrap.Elasticsearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParseFileServiceImpl {//} implements ParseFileService {

    private static final Logger logger = LoggerFactory.getLogger(ParseFileServiceImpl.class);

    @Autowired
    private PostService postService;

    @Autowired
    ConversationServiceImpl conversationService;

    @Autowired
    private SimpMessagingTemplate template;


    @Async
    public ParseFileResponseDTO parseFile(InputStream inputStream, String fileName, String conversationName, String weSocketId) {
        int postCount = 0;
        int lineCount = 0;

        String webSocketChannel = Channels.PARSEFILE.getName() + weSocketId;
        logger.info("websocket channel " + webSocketChannel);

        lineCount = 350;

        int feedbackStep = lineCount / 100;
        int lineRead = 0;
        Scanner scanner = new Scanner(inputStream);
        logger.info("Start reading " + lineCount + " lines of file");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lineRead++;
            postCount += (processLine(line, conversationName)) ? 1 : 0;
            if (postCount % feedbackStep == 0) {
                logger.info("Read " + lineRead + " of " + lineCount);
                this.template.convertAndSend(webSocketChannel, new Progress(lineRead, lineCount)); //sending to the channel

            }
        }
        scanner.close();

        //TODO envpyer autre que chose que -24
        this.template.convertAndSend(webSocketChannel, new Progress(-24, -24)); //sending to the channel

        logger.info(postCount + " post have been added from " + lineCount + " lines of the file ");//with path " + file.getAbsolutePath());

        conversationService.create(conversationName, postCount);

        return new ParseFileResponseDTO(postCount, lineCount);
    }

    private static int countLines(String filename) throws IOException {
        return countLines(new BufferedInputStream(new FileInputStream(filename)));
    }

    private static int countLines(InputStream inputStream) throws IOException {
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = inputStream.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            // inputStream.close(); //TODO remettre ca ?
        }
    }


    // 21/06/2015, 14:23 - Amandine Moulin: Moi aussi j'suis : - nue 😁
    private Boolean processLine(String line, String conversationName) {


        String regex = "(\\d{2}\\/\\d{2}\\/\\d{4})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.find()) return false;

        Post post = new Post();
        post.setAuthor(matcher.group(3));
        post.setContent(matcher.group(4));
        post.setConversationName(conversationName);

        String dateStr = matcher.group(1);
        String hourStr = matcher.group(2); //TODO hour
        //from dd/MM/YYYY to YYYY-MM-dd
        post.setDate(dateStr.substring(6, 10) + "-" + dateStr.substring(3, 5) + "-" + dateStr.substring(0, 2));

        postService.save(post);

        return true;

    }


}
