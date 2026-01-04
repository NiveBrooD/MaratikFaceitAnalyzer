package com.ramis.faceit_analyzer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FaceitHistory {
    @JsonProperty("items")
    List<Match> matches;
}
