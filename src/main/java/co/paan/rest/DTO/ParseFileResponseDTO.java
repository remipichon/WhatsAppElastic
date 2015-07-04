package co.paan.rest.DTO;

import java.util.ArrayList;

/**
 * Created by remi on 04/07/15.
 */
public class ParseFileResponseDTO {

    ArrayList<String> posts;


    public ParseFileResponseDTO(ArrayList<String> posts) {
        this.posts = posts;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }
}
