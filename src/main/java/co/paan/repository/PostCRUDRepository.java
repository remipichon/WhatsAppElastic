package co.paan.repository;

import co.paan.entities.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 04/07/15.
 */
public interface PostCRUDRepository extends CrudRepository<Post,String> {

    Long countByAuthor(String author);


    List<Post> findByConversationName(String conversationName);

    List<Post> findByConversationNameAndAuthor(String conversationName, String author );
}
