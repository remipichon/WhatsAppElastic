package co.paan.application;

import co.paan.configuration.ElasticsearchConfiguration;
import co.paan.entities.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by hungnguyen on 12/28/14.
 */
@Configuration
@ComponentScan(basePackages = "co.paan")
@EnableAutoConfiguration(exclude = {ElasticsearchConfiguration.class})
@EnableAsync
@SpringBootApplication
public class Application {


    public static void main(String args[]){
        SpringApplication.run(Application.class);


    }
}
