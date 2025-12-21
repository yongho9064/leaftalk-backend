package com.example.leaftalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LeafTalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeafTalkApplication.class, args);
    }

}
