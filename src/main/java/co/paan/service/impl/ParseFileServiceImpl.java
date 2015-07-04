package co.paan.service.impl;

import co.paan.service.ParseFileService;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Scanner;

/**
 * Created by remi on 04/07/15.
 */
@Service
public class ParseFileServiceImpl implements ParseFileService {

    private static final Logger logger = LoggerFactory.getLogger(ParseFileServiceImpl.class);

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
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(result.toString());

        return true;
    }

    
}
