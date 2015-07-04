package co.paan.controller;

import co.paan.rest.DTO.ParseFileResponseDTO;
import co.paan.service.impl.ParseFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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


    @RequestMapping("parsefile")
    public ParseFileResponseDTO parseFile(@RequestParam(value="filename", defaultValue="sample") String name){

        Boolean result = fileService.parseFile(name);

        return new ParseFileResponseDTO(new ArrayList<>(Arrays.asList("OK?",result.toString())));
    }



}
