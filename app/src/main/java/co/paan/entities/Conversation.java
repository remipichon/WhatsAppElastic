package co.paan.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Created by remi on 14/07/15.
 */
@Document(indexName = "conversation", type = "conversation", shards = 1, replicas = 0)
public class Conversation {
    @Id
    private String id;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
    private String name;


    @Field(type = FieldType.Date)
    private Date creationDate;

    @Field(type = FieldType.Boolean)
    private Boolean isParsed = false;

    public Conversation() { //required by Jackson
    }

    public Conversation(String name, Date creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    public Conversation(String name, Date creationDate, Integer postCount) {
        this.name = name;
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getParsed() {
        return isParsed;
    }

    public void setParsed(Boolean parsed) {
        isParsed = parsed;
    }
}
