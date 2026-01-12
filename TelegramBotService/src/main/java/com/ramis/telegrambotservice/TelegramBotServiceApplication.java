package com.ramis.telegrambotservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotServiceApplication.class, args);
    }

}
