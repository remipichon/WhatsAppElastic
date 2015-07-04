package co.paan.service.impl;

import co.paan.entities.Post;
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
    public Boolean parseFile(String fileName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        return parseFile(file);
    }

    @Override
    public Boolean parseFile(File file) {

        String result = "";
        try (Scanner scanner = new Scanner(file)) {
            logger.info("Start reading file with path", file.getAbsolutePath());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result += line;
                result += "\n";
                processLine(line);
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(result.toString());

        return true;
    }

    //TODO wr conversationName
    private void processLine(String line) {

        String[] split = line.split("-");
        if (split.length != 2) return;
        String[] split2 = split[1].split(":");

        if (split2.length != 2) return;

        Post post = new Post();
        post.setAuthor(split2[0]);
        post.setContent(split2[1]);

        postService.save(post);

    }


}
