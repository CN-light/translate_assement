package com.assessment.translate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.assessment.translate.mapper")
public class TranslateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslateApplication.class, args);
    }

}
