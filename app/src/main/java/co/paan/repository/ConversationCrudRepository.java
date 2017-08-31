package co.paan.repository;

import co.paan.entities.Conversation;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 14/07/15.
 */
public interface ConversationCrudRepository extends CrudRepository<Conversation, Integer> {

    List<Conversation> findByName(String conversationName);

}
