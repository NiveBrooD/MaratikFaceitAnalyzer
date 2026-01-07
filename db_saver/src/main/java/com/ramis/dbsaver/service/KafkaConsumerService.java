package com.ramis.dbsaver.service;

import com.ramis.dbsaver.model.StatsResponse;
import com.ramis.dbsaver.model.mapper.StatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final StatMapper statMapper;
    private final StatsService statsService;
    private final String TOPIC = "send_stats";

    @KafkaListener(topics = TOPIC, groupId = "stats")
    public void consume(StatsResponse statsResponse) {
         statsService.save(statMapper.to(statsResponse));
    }
}
