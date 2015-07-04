package co.paan.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

/**
 * Created by hungnguyen on 12/28/14.
 */
@Document(indexName = "conversation", type = "posts", shards = 1, replicas = 0)
public class Post {
    @Id
    private String id;
    private String author;
   // @Field(type= FieldType.Nested)
    private String content;
    private String conversationName;


    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
