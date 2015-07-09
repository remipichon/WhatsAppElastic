package co.paan.service.impl;

import co.paan.entities.Post;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.ParseFileService;
import co.paan.service.PostService;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Scanner;

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
            lineCount = countLines(path);
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


    private Boolean processLine(String line, String conversationName) {

        String[] split = line.split("-");
        if (split.length != 2) return false;
        String[] split2 = split[1].split(":");

        if (split2.length != 2) return false;

        Post post = new Post();
        post.setAuthor(split2[0]);
        post.setContent(split2[1]);
        post.setConversationName(conversationName);

        postService.save(post);

        return true;

    }


}
