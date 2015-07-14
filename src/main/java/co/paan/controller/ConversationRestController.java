package co.paan.controller;

import co.paan.entities.Conversation;
import co.paan.entities.Post;
import co.paan.rest.DTO.Author;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.impl.ConversationServiceImpl;
import co.paan.service.impl.ParseFileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by remi on 04/07/15.
 */

@RestController
@RequestMapping("/api/conversation")
public class ConversationRestController {

    private static final Logger logger = LoggerFactory.getLogger(ParseFileServiceImpl.class);


    @Autowired
    ParseFileServiceImpl fileService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ConversationServiceImpl conversationService;


    @RequestMapping("parsefile")
    public @ResponseBody //TODO DEV ONLY
    ParseFileResponseDTO parseFile(@RequestParam(value = "conversationName") String conversationName,
                                          @RequestParam(value = "filename") String fileName) {
        return fileService.parseFile(fileName,conversationName);
    }


    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    public
    @ResponseBody //TODO change response !
    String handleFileUpload(@RequestParam("conversationName") String conversationName,
                            @RequestParam("file") MultipartFile file) {

        if(conversationService.isNameAvailable(conversationName)) {

            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(new File("build/resources/main/"+conversationName)));
                    stream.write(bytes);
                    stream.close();


                    ParseFileResponseDTO parseFileResponseDTO = fileService.parseFile(conversationName, conversationName);

                    return "You successfully uploaded " + conversationName + " and parsed it with "+parseFileResponseDTO.getLineCount()+" "+parseFileResponseDTO.getPostCount();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "You failed to upload " + conversationName + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + conversationName + " because the file was empty.";
            }
        } else {
            return "File not parsed as the conversation name "+conversationName+"is already used.";
        }
    }


    //TODO DEBUG ONLY
    @RequestMapping("deleteAllPost")
    @Deprecated
    public void deleteAllPost() {
        elasticsearchTemplate.deleteIndex(Post.class);
        elasticsearchTemplate.createIndex(Post.class);
        elasticsearchTemplate.putMapping(Post.class);
        elasticsearchTemplate.refresh(Post.class, true);
    }


    @RequestMapping("getExistingConversationName")
    public ArrayList<Conversation> getExistingConversationName(){
        return conversationService.getExistingConversationName();
    }






}
