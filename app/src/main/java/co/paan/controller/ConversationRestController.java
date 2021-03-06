package co.paan.controller;

import co.paan.entities.Conversation;
import co.paan.entities.Post;
import co.paan.entities.Progress;
import co.paan.rest.DTO.ReceivedMailInfo;
import co.paan.service.impl.ConversationServiceImpl;
import co.paan.service.impl.EmailServiceImpl;
import co.paan.service.impl.ParseFileServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

@RestController
@RequestMapping("/api/conversation")
public class ConversationRestController {

    private static final Logger logger = LoggerFactory.getLogger(ConversationRestController.class);


    @Autowired
    ParseFileServiceImpl fileService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ConversationServiceImpl conversationService;

    @Autowired
    EmailServiceImpl emailService;

    @Value("${PUBLIC_ACCESS}")
    private String publicAccess;


    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(@RequestParam("conversationName") String conversationName,
                            @RequestParam("file") MultipartFile file) {
        String nothing;

        if(conversationService.isNameAvailable(conversationName)) {

            if (!file.isEmpty()) {
                try {

                    InputStream inputStream = file.getInputStream();
                    fileService.parseFile(inputStream, null,conversationName);
                    return new ResponseEntity<>("Websocket is no more available for UI based conversation",HttpStatus.OK);


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
        logger.info("Mail has been sent by " + fromName + " with email " + fromMail);

        //read attachment
        JSONArray attachments = mailingMsgJson.getJSONArray("attachments");
        if(attachments.length() != 1){
            return new ResponseEntity<String>("Mail from " + fromMail + " either doesn't have any attachments or have more than one. Skipping process",HttpStatus.BAD_REQUEST);
        }
        JSONObject conversationFile = attachments.getJSONObject(0);
        String fileName = conversationFile.getString("generatedFileName");
        logger.info("Mail attachment with conversation is named " + fileName);


        //decode attachment with Base64
        MultipartFile file = mrequest.getFile(fileName);
        if(file == null){
            return new ResponseEntity<>("Required request part '"+fileName+"' is not present.",HttpStatus.BAD_REQUEST);
        }
        InputStream decodedInputStream = null;
        InputStream otherDecodedInputStream = null;
        try {
            decodedInputStream = Base64.getMimeDecoder().wrap(file.getInputStream());
            otherDecodedInputStream = Base64.getMimeDecoder().wrap(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //debug only, uncommenting this will consume the input stream and make it empty for the parsing
        //printFileContent(file, decodedInputStream);

        String subject = null;
        try {
            subject = mailingMsgJson.getString("subject");
            subject = subject.replaceAll("[^a-zA-Z0-9.-]", "_");
            logger.info("Subject read ",subject);
        } catch (org.json.JSONException e){
            logger.info("Subject couldn't not be read from payload");
        }
        String conversationName;
        if(conversationService.isNameAvailable(subject)){
            conversationName = subject;
        } else {
            conversationName = conversationService.getRandomConversationName(subject);
        }
        logger.info("conversationName " + conversationName);

        //TODO there, sometime I got a
        /**
         demo_wae_spring_app.1.idzswp5rb52i@vps545030    | 2018-05-19 22:57:57.752 ERROR 32 --- [p-nio-80-exec-4] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed;
         nested exception is org.elasticsearch.client.transport.NoNodeAvailableException:
         None of the configured nodes were available:
         [[Fin Fang Foom][W2Dirg1lQ_uNcPXk0OSOPQ][5721b3834a71][inet[elasticsearch/10.0.4.5:9300]]]] with root cause
         demo_wae_spring_app.1.idzswp5rb52i@vps545030    |
         demo_wae_spring_app.1.idzswp5rb52i@vps545030    | org.elasticsearch.transport.NodeDisconnectedException: [Fin Fang Foom][inet[elasticsearch/10.0.4.5:9300]][search] disconnected
         demo_wae_spring_app.1.idzswp5rb52i@vps545030    |
         */
        //use unique conversation name
        Conversation conversation;
        try {
            conversation = conversationService.create(conversationName);
        } catch (org.elasticsearch.transport.NodeDisconnectedException nodeDisconnected){
            conversation = null;
        }

        //send mail with link to sender
        if(publicAccess != null && publicAccess != "") {
            logger.info("send an email to " + fromMail);
            String responseSubject = "No subject", body = "No body";
            if (conversation == null) {
                body = "Hello "+fromName+",\n\n\nWe received your chat but there was an issue with our system. Would mind trying a bit later ?" +
                        "\n\nBien à vous, \nA piece of code nicely written by a cool guy" +
                        "\n\n\n\nIf you want to know more, the middle encountered a nasty 'NodeDisconnectedException' because the backend IP changed. It will" +
                        "recover by itself within minutes. Try again in 10 minutes or so.";
            } else {
                String link = publicAccess + "#" + conversation.getName();
                body = "Hello "+fromName+",\n\n\nWe received your chat and we are now reading it (well, not the content, only dates and senders's name, we are not some freaks spying on you...).\n\n" +
                        "Please use this link to follow the parsing eta: "+ link + "\n" +
                        "Charts will automatically be drawn once it's ready. Consider keeping this link as it will allow you to quickly access your stats without sending your chat once again. Though I don't mind, if you love sending emails.\n\n" +
                        "Don't hesitate to pass WhatStat along if you liked .\n\n" +
                        "Bien à vous, \nA piece of code nicely written by a cool guy";
            }
            responseSubject = "We are now busy with your chat "+subject;
            logger.info("Sending email to " + fromMail + ".\nSubject: " + responseSubject + "\nBody:\n" + body);

            emailService.sendSimpleMessage(fromMail, responseSubject, body);
        } else {
            logger.warn("PUBLIC_ACCESS env is not defined, no mail has been sent to the sender");
        }

        if(conversation == null){
            return new ResponseEntity<String>("Most like there was a NodeDisconnectedException",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //read conversation async and parse it to ES async
        fileService.parseFile(decodedInputStream, otherDecodedInputStream, conversation, new ReceivedMailInfo(subject,fromName, fromMail));

        //return result (without waiting for the parse to be done)
        return new ResponseEntity<String>("Mail from " + fromMail + " is being parsed into " + conversation.getName() +". Get parse feedback with Websocket channel "+ fileService.getWebSocketChannel(conversation.getName()),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<? extends Object> get(@RequestParam(value = "conversationName") String conversationName){
        Conversation conversation = conversationService.getByName(conversationName);
        if(conversation == null)
            return new ResponseEntity<String>("Conversation named "+conversationName+" cannot be found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Conversation>(conversation, HttpStatus.OK);
    }


    private void printFileContent(MultipartFile file, InputStream decodedInputStream) {
        logger.info("Debug: file content not decoded");
        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            logger.info(line);
        }
        scanner2.close();

        logger.info("Debug: file content decoded");
        Scanner scanner = new Scanner(decodedInputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            logger.info(line);
        }
        scanner.close();
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

    //TODO DEBUG ONLY
    @RequestMapping("deleteAll")
    @Deprecated
    public void deleteAll() {
        elasticsearchTemplate.deleteIndex(Conversation.class);
        elasticsearchTemplate.deleteIndex(Progress.class);
        elasticsearchTemplate.deleteIndex(Post.class);
        elasticsearchTemplate.createIndex(Conversation.class);
        elasticsearchTemplate.createIndex(Progress.class);
        elasticsearchTemplate.createIndex(Post.class);
        elasticsearchTemplate.putMapping(Conversation.class);
        elasticsearchTemplate.putMapping(Progress.class);
        elasticsearchTemplate.putMapping(Post.class);
        elasticsearchTemplate.refresh(Conversation.class, true);
        elasticsearchTemplate.refresh(Progress.class, true);
        elasticsearchTemplate.refresh(Post.class, true);
    }

    //TODO
    //deleteAll


    @RequestMapping("getExistingConversationName")
    public ArrayList<Conversation> getExistingConversationName(){
        return conversationService.getExistingConversationName();
    }






}
