package com.example.learnmapbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication

public class LearnmapBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnmapBackendApplication.class, args);
    }

}


