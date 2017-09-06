package co.paan.service.impl;

import co.paan.entities.Conversation;
import co.paan.entities.Post;
import co.paan.repository.ConversationCrudRepository;
import co.paan.repository.ConversationRepository;
import co.paan.repository.PostCRUDRepository;
import co.paan.repository.PostRepository;
import co.paan.rest.DTO.Author;
import co.paan.service.ConversationService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
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
    PostCRUDRepository postCRUDRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ConversationRepository conversationRepository;

    @Autowired
    ConversationCrudRepository conversationCrudRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);

    private String[] poolOfNames = {"elephant", "castor", "girafe","poule","hippo","croco","vache","hibou"};


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

        logger.info("authors for conversation " + conversationName, authors);


        return authors;
    }


    @Override
    public Map<String, Long> getPostCountByAthors(String conversationName) {

        Map<String, Long> result = new HashMap<>();

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("conversationName", conversationName))
                .withIndices("conversation").withTypes("posts")
                .addAggregation(AggregationBuilders.terms("getPostCountByAthors").field("author").size(0))
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
            result.put(bucket.getKey(), bucket.getDocCount());
        }


        return result;
    }

    @Override
    public Map<String, Map<String, Float>> getContentStatAndPostCountByUser(String conversationName) {
        Map<String, Map<String, Float>> result = new HashMap<>();
        Map<String, Float> stat;
        String author;
        long docCount;
        Aggregation contentStats;


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("conversationName", conversationName))
                .withIndices("conversation").withTypes("posts")
                .addAggregation(AggregationBuilders.terms("postCount").field("author")
                        .subAggregation(AggregationBuilders.stats("content_stats").field("contentLength")))
                .build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {

                return response.getAggregations();
            }
        });

        Terms terms = aggregations.get("postCount");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            author = bucket.getKey();
            contentStats = bucket.getAggregations().get("content_stats");
            float count = ((InternalStats) contentStats).getCount();
            float max = (float) ((InternalStats) contentStats).getMax();
            float avg = (float) ((InternalStats) contentStats).getAvg();
            float sum = (float) ((InternalStats) contentStats).getSum();
            stat = new HashMap<>();
            stat.put("post_count", count);
            stat.put("content_max", max);
            stat.put("content_avg", avg);
            stat.put("content_sum", sum);
            result.put(author, stat);
        }

        return result;
    }

    @Override
    public Integer getTotalMessage(String conversationName) {
        return postCRUDRepository.countByConversationName(conversationName);
    }

    @Override
    public Double getTotalContent(String conversationName) {


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhraseQuery("conversationName", conversationName))
                .withIndices("conversation").withTypes("posts")
                .addAggregation(AggregationBuilders.stats("content_stats").field("contentLength"))
                .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        return ((InternalStats) aggregations.get("content_stats")).getSum();
    }

    @Override
    public Map<String, Map<String, Double>> getProportionMessageAndContentPerUser(String conversationName) {
        Map<String, Map<String, Double>> result = new HashMap<>();
        Map<String, Double> proportion;


        Map<String, Map<String, Float>> contentStatAndPostCountByUser = this.getContentStatAndPostCountByUser(conversationName);

        Integer totalMessage = this.getTotalMessage(conversationName);
        Double totalContent = this.getTotalContent(conversationName);

        for (Map.Entry<String, Map<String, Float>> entry : contentStatAndPostCountByUser.entrySet()) {
            String author = entry.getKey();
            Map<String, Float> stats = entry.getValue();
            Float post_count = stats.get("post_count");
            Float content_sum = stats.get("content_sum");
            proportion = new HashMap<>();
            proportion.put("post_proportion", post_count.doubleValue() / totalMessage);
            proportion.put("content_proportion", content_sum / totalContent);
            result.put(author, proportion);

        }


        return result;
    }

    @Override
    public Map<String, Long> getPostLengthByAuthors(String conversationname) {
        ArrayList<Author> authors = getAuthorsByConversationName(conversationname);

        Map<String, Long> result = new HashMap<>();


        for (Author author : authors) {
            Long lengthTotal = new Long(0);
            List<Post> byConversationNameAndAuthor = postCRUDRepository.findByConversationNameAndAuthor(null, author.getName()); //TODO use conversationName
            for (Post post : byConversationNameAndAuthor) {
                lengthTotal += post.getContent().length();
            }

            result.put(author.getName(), lengthTotal);
        }


        return result;
    }


    @Override
    public Map<String, Map<Integer, Integer>> getPostCountPerUserPerHour(String conversationName) {
        return null;
    }

    @Override
    public Map<String, Long> getPostCountPerUserBetweenDate(String conversationName, String startDate, String endDate) {
        Map<String, Long> result = new HashMap<>();
        String author;
        Long count;

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("conversationName", conversationName))
                .withIndices("conversation").withTypes("posts")
                .addAggregation(AggregationBuilders.filter("between_date").filter(FilterBuilders.rangeFilter("date").gte(startDate).lte(endDate))
                        .subAggregation(AggregationBuilders.terms("group_by_author").field("author").size(0)))
                .build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        InternalFilter internalFilter = aggregations.get("between_date");
        Aggregation aggregation = internalFilter.getAggregations().get("group_by_author");
        Collection<Terms.Bucket> buckets = ((StringTerms) aggregation).getBuckets();
        for (Terms.Bucket bucket : buckets) {
            author = bucket.getKey();
            count = bucket.getDocCount();
            result.put(author, count);
        }
        //result.put("total", internalFilter.getDocCount());

        return result;
    }

    @Override
    public Map<Integer, Map<String, Long>> getPostCountPerUserPerMonth(String conversationName, String year) {
        Map<Integer, Map<String, Long>> result = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            String monthStr = (month < 10) ? "0" + month : String.valueOf(month);
            String startDate = year + "-" + monthStr + "-" + "01";
            String endDate = year + "-" + monthStr + "-" + "25";
            Map<String, Long> postCountPerUserBetweenDate = getPostCountPerUserBetweenDate(conversationName, startDate, endDate);
            result.put(month, postCountPerUserBetweenDate);
        }

        return result;
    }

    @Override
    public Map<String, Map<Integer, Long>> getPostCountPerMonthPerUser(String conversationName, String year) {
        Map<String, Map<Integer, Long>> result = new HashMap<>();

        Map<Integer, Map<String, Long>> postCountPerUserPerMonth = getPostCountPerUserPerMonth(conversationName, year);

        for (Map.Entry<Integer, Map<String, Long>> integerMapEntry : postCountPerUserPerMonth.entrySet()) {
            Integer month = integerMapEntry.getKey();
            Map<String, Long> authorsCount = integerMapEntry.getValue();
            for (Map.Entry<String, Long> stringLongEntry : authorsCount.entrySet()) {
                String author = stringLongEntry.getKey();
                Long count = stringLongEntry.getValue();
                if (result.get(author) == null) {
                    Map<Integer, Long> monthsCount = new HashMap<>();
                    monthsCount.put(month, count);
                    result.put(author, monthsCount);
                } else {
                    result.get(author).put(month, count);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Map<Integer, Long>> getPostCountPerDayPerUser(String conversationName, String year, Integer month) {
        Map<String, Map<Integer, Long>> result = new HashMap<>();

        Map<Integer, Map<String, Long>> postCountPerUserPerDay = getPostCountPerUserPerDay(conversationName, year, month);

        for (Map.Entry<Integer, Map<String, Long>> integerMapEntry : postCountPerUserPerDay.entrySet()) {
            Integer day = integerMapEntry.getKey();
            Map<String, Long> authorsCount = integerMapEntry.getValue();
            for (Map.Entry<String, Long> stringLongEntry : authorsCount.entrySet()) {
                String author = stringLongEntry.getKey();
                Long count = stringLongEntry.getValue();
                if (result.get(author) == null) {
                    Map<Integer, Long> monthsCount = new HashMap<>();
                    monthsCount.put(day, count);
                    result.put(author, monthsCount);
                } else {
                    result.get(author).put(day, count);
                }
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Map<String, Long>> getPostCountPerUserPerDay(String conversationName, String year, Integer month) {
        Map<Integer, Map<String, Long>> result = new HashMap<>();

        for (int day = 1; day <= 28; day++) { //TODO day's limit depends on month number
            String monthStr = (month < 10) ? "0" + month : String.valueOf(month);
            String dayStr = (day < 10) ? "0" + day : String.valueOf(day);
            String startDate = year + "-" + monthStr + "-" + dayStr;
            String endDate = year + "-" + monthStr + "-" + dayStr;
            Map<String, Long> postCountPerUserBetweenDate = getPostCountPerUserBetweenDate(conversationName, startDate, endDate);
            result.put(day, postCountPerUserBetweenDate);
        }

        return result;
    }

    @Override
    public ArrayList<Conversation> getExistingConversationName() { //TODO penible de devoir faire ca, investiguer pour pouvoir retoutner direcrement le resultat
        ArrayList<Conversation> result = new ArrayList<>();
        Iterable<Conversation> all = conversationRepository.findAll();
        for (Conversation conversation : all) {
            result.add(conversation);
        }
        return result;
    }

    @Override
    public Conversation create(String conversationName, int postCount) {
        return conversationRepository.save(new Conversation(conversationName, new Date(), postCount));
    }

    @Override
    public Conversation create(String conversationName) {
        if(this.isNameAvailable(conversationName))
            return conversationRepository.save(new Conversation(conversationName, new Date()));
        return null;
    }

    @Override
    public Conversation setParsed(Conversation conversation){
        conversation.setParsed(true);
        return conversationRepository.save(conversation);
    }

    @Override
    public boolean isNameAvailable(String conversationName) {
        return conversationCrudRepository.findByName(conversationName).isEmpty(); //TODO il y a certainement bien mieux à faire
    }

    @Override
    public Conversation getByName(String name){
        return conversationCrudRepository.findByName(name).get(0);
    }

    @Override
    public Conversation getById(String id){
        return conversationCrudRepository.findOne(Integer.valueOf(id));
    }

    @Override
    public String getRandomConversationName(){
        Random r = new Random();
        int one = r.nextInt(poolOfNames.length);
        int two = r.nextInt(poolOfNames.length);
        String conversationName = poolOfNames[one] + "_" + poolOfNames[two];
        if(!this.isNameAvailable(conversationName)){
            conversationName = getRandomConversationName();
        }
        return conversationName;
    }

}
