package com.ramis.dbsaver.service;

import com.ramis.dbsaver.model.Stats;
import com.ramis.dbsaver.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    public void save(Stats stats){
        if(!statsRepository.existsByDate(stats.getDate())) {
            statsRepository.save(stats);
            log.info("Stats saved successfully.");
        }
        else {
            log.warn("Stats with date {} already exists", stats.getDate());
        }
    }
}
