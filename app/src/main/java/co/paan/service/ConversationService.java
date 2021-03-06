package co.paan.service;

import co.paan.entities.Conversation;
import co.paan.rest.DTO.Author;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by remi on 04/07/15.
 */
public interface ConversationService {

    ArrayList<Author> getAuthorsByConversationName(String conversationName);

    Map<String, Long> getPostCountByAthors(String conversationname);

    Map<String, Map<String, Double>> getProportionMessageAndContentPerUser(String conversationName);

    Map<String,Long> getPostLengthByAuthors(String conversationname);

    Map<String, Map<String, Float>> getContentStatAndPostCountByUser(String conversationname);

    Integer getTotalMessage(String conversationName);

    Double getTotalContent(String conversationName);

    Map<String, Map<Integer, Integer>> getPostCountPerUserPerHour(String conversationName);

    Map<String,Long> getPostCountPerUserBetweenDate(String conversationName, String startDate, String endDate);

    Map<Integer, Map<String, Long>> getPostCountPerUserPerMonth(String conversationName, String year);

    Map<String, Map<Integer, Long>> getPostCountPerMonthPerUser(String conversationName, String year);

    Map<String, Map<Integer, Long>> getPostCountPerDayPerUser(String conversationName, String year, Integer month);

    Map<Integer, Map<String, Long>> getPostCountPerUserPerDay(String conversationName, String year, Integer month);

    ArrayList<Conversation> getExistingConversationName();

    Conversation create(String conversationName, int postCount);

    Conversation create(String conversationName);

    Conversation setParsed(Conversation conversation);

    boolean isNameAvailable(String conversationName);

    Conversation getByName(String name);

    Conversation getById(String id);

    String getRandomConversationName();

    String getRandomConversationName(String prefix);
}
