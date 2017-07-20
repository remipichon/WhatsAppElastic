package co.paan.controller;

import co.paan.entities.Conversation;
import co.paan.entities.Post;
import co.paan.service.impl.ConversationServiceImpl;
import co.paan.service.impl.ParseFileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/conversation")
public class ConversationRestController {

    private static final Logger logger = LoggerFactory.getLogger(ParseFileServiceImpl.class);


    @Autowired
    ParseFileServiceImpl fileService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ConversationServiceImpl conversationService;

    private SecureRandom random = new SecureRandom();



    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(@RequestParam("conversationName") String conversationName,
                            @RequestParam("file") MultipartFile file) {
        String nothing;

        if(conversationService.isNameAvailable(conversationName)) {

            if (!file.isEmpty()) {
                try {

                    String webSocketId = new BigInteger(130, random).toString(32);
                    InputStream inputStream = file.getInputStream();
                    fileService.parseFile(inputStream,conversationName, conversationName,webSocketId);
                    return new ResponseEntity<>(webSocketId,HttpStatus.OK);


                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>("You failed to upload " + conversationName + " => " + e.getMessage(),HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("You failed to upload " + conversationName + " because the file was empty.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("File not parsed as the conversation name "+conversationName+"is already used.", HttpStatus.BAD_REQUEST);
        }
    }


    //TODO DEBUG ONLY
    @RequestMapping("deleteAllPost")
    @Deprecated
    public void deleteAllPost() {
        elasticsearchTemplate.deleteIndex(Post.class);
        elasticsearchTemplate.createIndex(Post.class);
        elasticsearchTemplate.putMapping(Post.class);
        elasticsearchTemplate.refresh(Post.class);
    }


    @RequestMapping("getExistingConversationName")
    public ArrayList<Conversation> getExistingConversationName(){
        return conversationService.getExistingConversationName();
    }






}
