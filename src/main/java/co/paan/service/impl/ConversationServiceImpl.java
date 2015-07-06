package co.paan.service.impl;

import co.paan.entities.Post;
import co.paan.repository.PostCRUDRepository;
import co.paan.repository.PostRepository;
import co.paan.rest.DTO.Author;
import co.paan.service.ConversationService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
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

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);


    @Override
    public ArrayList<Author> getAuthorsByConversationName(String conversationName) {
        ArrayList<Author> authors = new ArrayList<>();


        Iterable<Post> allPost = postCRUDRepository.findByConversationName(conversationName);
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
    public Map<String, Long> getPostCountByAthors(String conversationName) {

        Map<String, Long> result = new HashMap<>();

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("conversationName", conversationName))
                .withIndices("conversation").withTypes("posts")
                .addAggregation(AggregationBuilders.terms("getPostCountByAthors").field("author"))
                .build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {

                return response.getAggregations();
            }
        });

        Terms terms = aggregations.get("getPostCountByAthors");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            result.put(bucket.getKey(),bucket.getDocCount());
        }


        return result;
    }

    @Override
    public Map<String, Long> getPostLengthByAuthors(String conversationname) {
        ArrayList<Author> authors = getAuthorsByConversationName(conversationname);

        Map<String, Long> result = new HashMap<>();


        for (Author author : authors) {
            Long lengthTotal = new Long(0);
            List<Post> byConversationNameAndAuthor = postCRUDRepository.findByConversationNameAndAuthor(null,author.getName()); //TODO use conversationName
            for (Post post : byConversationNameAndAuthor) {
                lengthTotal += post.getContent().length();
            }

            result.put(author.getName(), lengthTotal);
        }


        return result;
    }



}
