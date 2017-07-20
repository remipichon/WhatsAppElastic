package co.paan.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by hungnguyen on 12/28/14.
 */
@Configuration
@ComponentScan(basePackages = "co.paan")
@EnableAutoConfiguration
@EnableAsync
@SpringBootApplication
public class Application {


    public static void main(String args[]){
        SpringApplication.run(Application.class);


    }
}
