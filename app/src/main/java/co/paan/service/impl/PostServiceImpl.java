package co.paan.service.impl;

import co.paan.entities.Post;
import co.paan.repository.PostRepository;
import co.paan.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hungnguyen on 12/28/14.
 */
@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;

    @Override
    public Post save(Post post) {
        post = postRepository.save(post);
        return post;
    }

    @Override
    public Post findOne(String id) {
        return postRepository.findOne(id);
    }

    @Override
    public Iterable<Post> findAll() {
        return postRepository.findAll();
    }

}
