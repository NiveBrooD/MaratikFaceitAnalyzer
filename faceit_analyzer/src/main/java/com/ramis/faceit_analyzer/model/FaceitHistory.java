package com.ramis.faceit_analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FaceitHistory(
        @JsonProperty("items")
        List<Match> matches
) {
    public record Match(
            @JsonProperty("match_id")
            String matchId,

            @JsonProperty("finished_at")
            Long finishedAt
    ) {}
}
