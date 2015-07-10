package co.paan.service.impl;

import co.paan.entities.Post;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.ParseFileService;
import co.paan.service.PostService;
import com.google.common.io.CharStreams;
import org.elasticsearch.bootstrap.Elasticsearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by remi on 04/07/15.
 */
@Service
public class ParseFileServiceImpl implements ParseFileService {

    private static final Logger logger = LoggerFactory.getLogger(ParseFileServiceImpl.class);

    @Autowired
    private PostService postService;

    @Override
    public ParseFileResponseDTO parseFile(String fileName, String conversationName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        int postCount = 0;
        int lineCount = 0;
        String path = classLoader.getResource(fileName).getPath();

        try {
            lineCount = countLines(path)+1;
        } catch (IOException e) {
            e.printStackTrace(); //TODO capter ca dans le controleur d'error
        }

        try (Scanner scanner = new Scanner(file)) {
            logger.info("Start reading " + lineCount + " lines of file with path", file.getAbsolutePath());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                postCount += (processLine(line, conversationName)) ? 1 : 0;
            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(postCount + " post have been added from " + lineCount + " lines of the file with path " + file.getAbsolutePath());

        return new ParseFileResponseDTO(postCount, lineCount);
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }


   // 21/06/2015, 14:23 - Amandine Moulin: Moi aussi j'suis : - nue 😁
    private Boolean processLine(String line, String conversationName) {


        String regex =  "(\\d{2}\\/\\d{2}\\/\\d{4})[,]\\s(\\d(?:\\d)?:\\d{2} )-\\s([^:]*):(.*?)(?=\\s*\\d{2}\\/|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if(!matcher.find() ) return false;

        Post post = new Post();
        post.setAuthor(matcher.group(3));
        post.setContent(matcher.group(4));
        post.setConversationName(conversationName);

        String dateStr = matcher.group(1);
        String hourStr = matcher.group(2); //TODO hour
        //from dd/MM/YYYY to YYYY-MM-dd
        post.setDate(dateStr.substring(6,10)+"-"+ dateStr.substring(3,5)+"-"+dateStr.substring(0,2));

        postService.save(post);

        return true;

    }


}
