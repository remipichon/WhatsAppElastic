package co.paan.controller;

import co.paan.entities.Post;
import co.paan.rest.DTO.Author;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.impl.ConversationServiceImpl;
import co.paan.service.impl.ParseFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by remi on 04/07/15.
 */

@RestController
@RequestMapping("/api/conversation")
public class ConversationRestController {

    @Autowired
    ParseFileServiceImpl fileService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ConversationServiceImpl conversationService;


    @RequestMapping("parsefile")
    public @ResponseBody
    ParseFileResponseDTO parseFile(@RequestParam(value = "conversationName") String conversationName,
                                          @RequestParam(value = "filename") String fileName) {
        return fileService.parseFile(fileName,conversationName);
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


    @RequestMapping("getauthors")
    public ArrayList<Author> getConversationParticipants(@RequestParam(value = "conversationName") String conversationName) {
        return conversationService.getAuthorsByConversationName(conversationName);
    }






}
