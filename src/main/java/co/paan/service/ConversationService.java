package co.paan.service;

import co.paan.rest.DTO.Author;

import java.util.ArrayList;

/**
 * Created by remi on 04/07/15.
 */
public interface ConversationService {

    ArrayList<Author> getAuthorsByConversationName(String conversationName);
}
