package com.ramis.faceit_analyzer.service;

import com.ramis.faceit_analyzer.model.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, StatsResponse> kafkaTemplate;
    private static final String TOPIC = "send_stats";

    public void sendStats(StatsResponse statsResponse) {
        String key = statsResponse.date().toString() + "-" + UUID.randomUUID();
        kafkaTemplate.send(TOPIC, key, statsResponse);
    }
}
