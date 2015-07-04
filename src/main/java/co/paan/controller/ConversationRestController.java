package co.paan.controller;

import co.paan.entities.Post;
import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.impl.ParseFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

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


    @RequestMapping("parsefile")
    public ParseFileResponseDTO parseFile(@RequestParam(value = "filename", defaultValue = "sample") String name) {

        Boolean result = fileService.parseFile(name);

        return new ParseFileResponseDTO(new ArrayList<>(Arrays.asList("OK?", result.toString())));
    }


    //    //TODO DEBUG ONLY
    @RequestMapping("deleteAllPost")
    public void deleteAllPost() {

        elasticsearchTemplate.deleteIndex(Post.class);
        elasticsearchTemplate.createIndex(Post.class);
        elasticsearchTemplate.putMapping(Post.class);
        elasticsearchTemplate.refresh(Post.class, true);
    }


}
