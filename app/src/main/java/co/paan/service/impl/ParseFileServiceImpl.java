package co.paan.service.impl;

import co.paan.configuration.Channels;
import co.paan.entities.Post;
import co.paan.entities.Progress;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

        //lineCount = this.countLines(inputStream); //ce truc fuck up the inputstream...



        int feedbackStep = 1;//lineCount / 100;
        int lineRead = 0;
        Scanner scanner = new Scanner(inputStream);
        logger.info("Start reading " + lineCount + " lines of file");
        while (scanner.hasNextLine()) {
            if(lineRead > 100) feedbackStep = 100;
            if(lineRead > 1000) feedbackStep = 1000;
            //if(lineRead > 10000) feedbackStep = 10000;
            String line = scanner.nextLine();
            lineRead++;
            postCount += (processLine(line, conversationName)) ? 1 : 0;
            if (postCount % feedbackStep == 0) {
                logger.info("Read " + lineRead + " of " + lineCount);
                this.template.convertAndSend(webSocketChannel, new Progress(lineRead, lineCount)); //sending to the channel

            }
        }
        scanner.close();

        lineCount = lineRead;

        //TODO envpyer autre que chose que -24
        this.template.convertAndSend(webSocketChannel, new Progress(-24, -24)); //sending to the channel

        logger.info(postCount + " post have been added from " + lineCount + " lines of the file ");//with path " + file.getAbsolutePath());

        conversationService.create(conversationName, postCount);

        return new ParseFileResponseDTO(postCount, lineCount);
    }

    private static int countLines(String filename) throws IOException {
        return countLines(new BufferedInputStream(new FileInputStream(filename)));
    }

    private static int countLines(InputStream inputStream){
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            try {
                while ((readChars = inputStream.read(c)) != -1) {
                    empty = false;
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            try {
                inputStream.close(); //TODO remettre ca ?
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    // 21/06/2015, 14:23 - Amandine Moulin: Moi aussi j'suis : - nue 😁
    private Boolean processLine(String line, String conversationName) {
        String[] regexes = new String[4];
        String[] dateRegexes = new String[4];
        Pattern pattern;
        Matcher matcherLine, matcherDate;
        String dateStr = null;
        //DD/MM/YYYY, HH:mm - AUTHOR NAME:
        String normalRegex = "(\\d{2}\\/\\d{2}\\/\\d{4})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        String normalDateRegex = "^(\\d{2})\\/(\\d{2})\\/(\\d{4})";
        //M/DD/YY, HH:mm - AUTHOR NAME:
        regexes[0] = "^(\\d{1}\\/\\d{2}\\/\\d{2})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        dateRegexes[0] = "^(\\d{1})\\/(\\d{2})\\/(\\d{2})";
        //M/D/YY, HH:mm - AUTHOR NAME:
        regexes[1] = "^(\\d{1}\\/\\d{1}\\/\\d{2})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        dateRegexes[1] = "^(\\d{1})\\/(\\d{1})\\/(\\d{2})";
        //MM/DD/YY, HH:mm - AUTHOR NAME:
        regexes[2] = "^(\\d{2}\\/\\d{2}\\/\\d{2})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        dateRegexes[2] = "^(\\d{2})\\/(\\d{2})\\/(\\d{2})";
        //MM/D/YY, HH:mm - AUTHOR NAME:
        regexes[3] = "^(\\d{2}\\/\\d{1}\\/\\d{2})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        dateRegexes[3] = "^(\\d{2})\\/(\\d{1})\\/(\\d{2})";

        //find regular line
        pattern = Pattern.compile(normalRegex);
        matcherLine = pattern.matcher(line);

        if (matcherLine.find()) {
            //DD/MM/YYYY
            pattern = Pattern.compile(normalDateRegex);
            matcherDate = pattern.matcher(matcherLine.group(1));
            matcherDate.find(); //should we always true
            //from dd/MM/YYYY to YYYY-MM-dd
            dateStr = matcherDate.group(3) + "-" + matcherDate.group(2) + "-" + matcherDate.group(1);
        } else {
            //trying with uncommon regex
            for(int i = 0; i < regexes.length; i++){
                pattern = Pattern.compile(regexes[i]);
                matcherLine = pattern.matcher(line);
                if (matcherLine.find()) {
                    pattern = Pattern.compile(dateRegexes[i]);
                    matcherDate = pattern.matcher(line);
                    matcherDate.find();
                    dateStr = "20" + matcherDate.group(3) + "-" +  //will not work for the third milena
                            ((matcherDate.group(1).length() == 1)? "0"+matcherDate.group(1): matcherDate.group(1))
                            + "-" +
                            ((matcherDate.group(2).length() == 1)? "0"+matcherDate.group(2): matcherDate.group(2));

                    break;
                }
            }
            if(dateStr == null) return false;
        }


        Post post = new Post();
        post.setAuthor(matcherLine.group(3));
        post.setContent(matcherLine.group(4));
        post.setConversationName(conversationName);

        String hourStr = matcherLine.group(2); //TODO hour

        post.setDate(dateStr);

        postService.save(post);

        return true;

    }


}
