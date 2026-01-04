package com.ramis.faceit_analyzer.model;

import java.util.List;

public record StatsResponse(

        Integer gamesPlayed,
        Integer wins,
        Integer loss,
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
