package com.ramis.dbsaver.repository;

import com.ramis.dbsaver.model.Stats;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface StatsRepository extends CrudRepository<Stats, Long> {

    boolean existsByDate(LocalDate date);

}
