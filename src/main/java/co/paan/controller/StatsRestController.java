package co.paan.controller;


import co.paan.service.impl.ConversationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsRestController {


    @Autowired
    ConversationServiceImpl conversationService;

    /**
     *
     * @param conversationname
     * @return USERNAME : { STATNAME: VALUE... }
     */
    @RequestMapping("contentStatAndPostCountByUser")
    public Map<String,Map<String,Float>> getContentStatAndPostCountByUser(@RequestParam(value = "conversationName") String conversationname){
        return conversationService.getContentStatAndPostCountByUser(conversationname);
    }


    @RequestMapping("proportionMessageAndContentPerUser")
    public Map<String, Map<String, Double>> getProportionMessageAndContentPerUser(@RequestParam(value = "conversationName") String conversationName){
        return conversationService.getProportionMessageAndContentPerUser(conversationName);
    }
}
