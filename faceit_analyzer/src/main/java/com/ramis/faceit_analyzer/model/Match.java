package com.ramis.faceit_analyzer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Match {

    @JsonProperty("match_id")
    private String matchId;

    @JsonProperty("finished_at")
    private Long finishedAt;
}
