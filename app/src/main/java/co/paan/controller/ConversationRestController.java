package co.paan.controller;

import co.paan.entities.Conversation;
import co.paan.entities.Post;
import co.paan.service.impl.ConversationServiceImpl;
import co.paan.service.impl.ParseFileServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

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

    @RequestMapping(value = "mailhook", method = RequestMethod.POST, consumes = {"multipart/*"})
    public ResponseEntity<String> handleFileUpload2(@RequestPart("mailinMsg") MultipartFile mailinMsg,
                                                    MultipartHttpServletRequest mrequest) {

        //read mailin payload at once
        String jsonString = "";
        try {
            jsonString = new Scanner(mailinMsg.getInputStream()).useDelimiter("\\Z").next();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //convert mailin to json and extract info
        JSONObject mailingMsgJson = new JSONObject(jsonString);
        String fromMail = mailingMsgJson.getJSONArray("from").getJSONObject(0).getString("address");
        String fromName = mailingMsgJson.getJSONArray("from").getJSONObject(0).getString("name");
        System.out.println("Mail has been send by " + fromName + " with email " + fromMail);

        //read attachment
        JSONArray attachments = mailingMsgJson.getJSONArray("attachments");
        JSONObject conversationFile = attachments.getJSONObject(0);//if more than one attachment, we don't care
        String fileName = conversationFile.getString("generatedFileName");
        System.out.println("Mail attachment with conversation is named " + fileName);


        //decode attachment with Base64
        MultipartFile file = mrequest.getFile(fileName);
        if(file == null){
            return new ResponseEntity<>("Required request part '"+fileName+"' is not present.",HttpStatus.BAD_REQUEST);
        }
        InputStream decodedInputStream = null;
        try {
            decodedInputStream = Base64.getMimeDecoder().wrap(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //debug only, uncommenting this will consume the input stream and make it empty for the parsing
//        System.out.println("Debug: file content not decoded");
//        Scanner scanner2 = null;
//        try {
//            scanner2 = new Scanner(file.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        while (scanner2.hasNextLine()) {
//            String line = scanner2.nextLine();
//            System.out.println(line);
//        }
//        scanner2.close();
//
//        System.out.println("Debug: file content decoded");
//        Scanner scanner = new Scanner(decodedInputStream);
//        while (scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            System.out.println(line);
//        }
//        scanner.close();


        //use unique conversation ID

        //send mail with link to sender

        //read conversation and parse it to ES async
        String webSocketId = new BigInteger(130, random).toString(32);
        fileService.parseFile(decodedInputStream,null, "TEST",webSocketId);


        //return result (without waiting for the parse to be done)
        return new ResponseEntity<String>("Mail from " + fromMail + " is being parsed.",HttpStatus.OK);
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
