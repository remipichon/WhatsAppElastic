package co.paan.service.impl;

import co.paan.entities.Post;
import co.paan.repository.PostCRUDRepository;
import co.paan.repository.PostRepository;
import co.paan.rest.DTO.Author;
import co.paan.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by remi on 04/07/15.
 */
@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    PostCRUDRepository postCRUDRepository ;

    @Autowired
    PostRepository postRepository;

    private static final Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);


    @Override
    public ArrayList<Author> getAuthorsByConversationName(String conversationName) {
        ArrayList<Author> authors = new ArrayList<>();


        //TODO utilisaer converastionName !
        Iterable<Post> allPost = postCRUDRepository.findByConversationName(null);
        Iterable<Post> allPost2 = postRepository.findAll();

        Set<Author> authorSet = new HashSet<>();

        for (Post post : allPost) {
            authorSet.add(new Author(post.getAuthor()));
        }

        authors.addAll(authorSet);

        logger.info("authors for conversation "+conversationName,authors);


        return authors;
    }

    @Override
    public Map<String, Long> getPostCountByAthors(String conversationname) {
        ArrayList<Author> authors = getAuthorsByConversationName(conversationname);

        Map<String, Long> result = new HashMap<>();


        for (Author author : authors) {
            result.put(author.getName(), postCRUDRepository.countByAuthor(author.getName()));
        }


        return result;
    }
}
