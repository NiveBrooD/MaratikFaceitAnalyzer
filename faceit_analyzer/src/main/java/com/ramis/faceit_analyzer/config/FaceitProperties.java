package com.ramis.faceit_analyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "faceit")
public class FaceitProperties {

    private String apiUrl;
    private String nickname;
    private String apiKey;
    private String id;
}
