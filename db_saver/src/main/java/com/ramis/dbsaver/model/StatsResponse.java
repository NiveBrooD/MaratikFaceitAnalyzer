package com.ramis.dbsaver.model;

import java.time.LocalDate;
import java.util.List;

public record StatsResponse(

        Integer gamesPlayed,
        Integer wins,
        Integer loss,
        LocalDate date,
        Double avgKd,
        Double avgAdr,
        Double avgKills,
        Double avgDeaths,
        Double avgAssists,
        Double avgHeadshots,
        Double avgDoubleKills,
        Double avgTripleKills,
        Double avgQuadroKills,
        Double avgPentaKills,
        List<String> matches
) {}
