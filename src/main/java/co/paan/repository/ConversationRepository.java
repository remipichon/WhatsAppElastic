package co.paan.repository;

import co.paan.entities.Conversation;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import java.util.ArrayList;

/**
 * Created by remi on 14/07/15.
 */
public interface ConversationRepository extends ElasticsearchCrudRepository<Conversation,String> {
}
