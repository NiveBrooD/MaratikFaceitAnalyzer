package com.ramis.telegrambotservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

    private String apiUrl;
    private String webhookPath;
    private String botName;
    private String botToken;
}
