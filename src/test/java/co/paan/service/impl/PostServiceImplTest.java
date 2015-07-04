package co.paan.service.impl;

import co.paan.application.Application;
import co.paan.domain.Greeting;
import co.paan.entities.Post;
import co.paan.entities.Tag;
import co.paan.service.PostService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PostServiceImplTest{
    @Autowired
    private PostService postService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GreetingServiceImpl greetingService;

    @Before
    public void before() {
        elasticsearchTemplate.deleteIndex(Post.class);
        elasticsearchTemplate.createIndex(Post.class);
        elasticsearchTemplate.putMapping(Post.class);
        elasticsearchTemplate.refresh(Post.class, true);
    }

    @Test
    public void testService(){
        Greeting greeting = greetingService.getGreeting();

        assertNotNull(greeting);
    }

    //@Test
    public void testSave() throws Exception {
        Tag tag = new Tag();
        tag.setId("1");
        tag.setName("tech");
        Tag tag2 = new Tag();
        tag2.setId("2");
        tag2.setName("elasticsearch");

        Post post = new Post();
        post.setId("1");
        post.setContent("Bigining with spring boot application and elasticsearch");
        postService.save(post);

        assertThat(post.getId(), notNullValue());

        Post post2 = new Post();
        post2.setId("1");
        post2.setContent("Bigining with spring boot application");
        postService.save(post);
        assertThat(post2.getId(), notNullValue());




    }

    public void testFindOne() throws Exception {

    }

    public void testFindAll() throws Exception {

    }
    @Test
    public void testFindByTagsName() throws Exception {
        Tag tag = new Tag();
        tag.setId("1");
        tag.setName("tech");
        Tag tag2 = new Tag();
        tag2.setId("2");
        tag2.setName("elasticsearch");

        Post post = new Post();
        post.setId("1");
        post.setContent("Bigining with spring boot application and elasticsearch");
        postService.save(post);



        Post post2 = new Post();
        post2.setId("1");
        post2.setContent("Bigining with spring boot application");
        postService.save(post);

    }
}
