package com.ramis.telegrambotservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramis.telegrambotservice.dto.NoStatistics;
import com.ramis.telegrambotservice.dto.Request;
import com.ramis.telegrambotservice.dto.StatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceitService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public Request getYesterdayStatistics() {
        String body = restClient.get()
                .uri("/days/yesterday")
                .retrieve()
                .body(String.class);
        return getStatistics(body);
    }

    public Request getLastFiveMatchesStatistics() {
        String body = restClient.get()
                .uri("/matches/last_five")
                .retrieve()
                .body(String.class);
        return getStatistics(body);
    }

    public Request getStatisticForDate(String date) {
        String body = restClient.get()
                .uri("/days/" + date)
                .retrieve()
                .body(String.class);
        return getStatistics(body);
    }

    private Request getStatistics(String body) {
        if (body == null) {
            return new NoStatistics("Empty body, Something went wrong");
        }
        if (body.contains("message")) {
            try {
                return objectMapper.readValue(body, NoStatistics.class);
            } catch (JsonProcessingException e) {
                log.error("Error parsing JSON response", e);
                return new NoStatistics("Couldn't parse, Something went wrong");
            }
        }
        if (body.contains("avgPentaKills")) {
            try {
                return objectMapper.readValue(body, StatsResponse.class);
            } catch (JsonProcessingException e) {
                log.error("Error parsing JSON response", e);
                return new NoStatistics("Couldn't parse, Something went wrong");
            }
        }
        return new NoStatistics("Something went wrong");
    }
}
