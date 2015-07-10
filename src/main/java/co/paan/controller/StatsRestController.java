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
     * @param conversationname
     * @return USERNAME : { STATNAME: VALUE... }
     */
    @RequestMapping("contentStatAndPostCountByUser")
    public Map<String, Map<String, Float>> getContentStatAndPostCountByUser(@RequestParam(value = "conversationName") String conversationname) {
        return conversationService.getContentStatAndPostCountByUser(conversationname);
    }


    @RequestMapping("proportionMessageAndContentPerUser")
    public Map<String, Map<String, Double>> getProportionMessageAndContentPerUser(@RequestParam(value = "conversationName") String conversationName) {
        return conversationService.getProportionMessageAndContentPerUser(conversationName);
    }

    @RequestMapping("postCountPerUserPerHour")
    public Map<String, Map<Integer, Integer>> getPostCountPerUserPerHour(@RequestParam(value = "conversationName") String conversationName) {
        return conversationService.getPostCountPerUserPerHour(conversationName);
    }

    @RequestMapping("postCountPerUserBetweenDate")
    public Map<String, Long> getPostCountPerUserBetweenDate(
            @RequestParam(value = "conversationName") String conversationName,
            @RequestParam(value = "beginDate") String startDate,
            @RequestParam(value = "endDate") String endDate) {
        return conversationService.getPostCountPerUserBetweenDate(conversationName, startDate, endDate);
    }

    /*
    {
        1 : {
                author: count,
                .....
        },
        2 : {}, ... ,
        12: {}
    }
     */
    @RequestMapping("postCountPerUserPerMonth")
    public Map<Integer, Map<String, Long>> getPostCountPerUserPerMonth(
            @RequestParam(value = "conversationName") String conversationName,
            @RequestParam(value = "year") String year) {
        return conversationService.getPostCountPerUserPerMonth(conversationName, year);
    }

    /*
    {
        author : {
                 1 : count,
                 2 : count, ... ,
                12:
        },
        .....
    }
     */
    @RequestMapping("postCountPerMonthPerUser")
    public Map<String, Map<Integer, Long>> getPostCountPerMonthPerUser(
            @RequestParam(value = "conversationName") String conversationName,
            @RequestParam(value = "year") String year) {
        return conversationService.getPostCountPerMonthPerUser(conversationName, year);
    }
}
