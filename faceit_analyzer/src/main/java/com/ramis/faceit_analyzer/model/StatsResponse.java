package com.ramis.faceit_analyzer.model;

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
) {
    public StatsResponse {
        avgKd = format(avgKd);
        avgAdr = format(avgAdr);
        avgKills = format(avgKills);
        avgDeaths = format(avgDeaths);
        avgAssists = format(avgAssists);
        avgHeadshots = format(avgHeadshots);
        avgDoubleKills = format(avgDoubleKills);
        avgTripleKills = format(avgTripleKills);
        avgQuadroKills = format(avgQuadroKills);
        avgPentaKills = format(avgPentaKills);
    }

    private static double format(Double value) {
        if (value == null || value.isNaN()) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }
}
