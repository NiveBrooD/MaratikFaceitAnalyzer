package com.ramis.faceit_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MatchStatistic {

    private String matchId;
    private Integer winning;
    private Double kdRatio;
    private Double adr;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer headshotsPercentage;
    private Integer doubleKills;
    private Integer tripleKills;
    private Integer quadroKills;
    private Integer pentaKills;
}
