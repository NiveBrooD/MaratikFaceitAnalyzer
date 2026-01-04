package com.ramis.faceit_analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchResponse(List<Round> rounds) {

    public record Round(
            @JsonProperty("match_id")
            String matchId,

            List<Team> teams
    ) {
        public record Team(
                @JsonProperty("team_stats")
                TeamStats teamStats,

                @JsonProperty("players")
                List<Player> players
        ) {
            public record TeamStats(
                    @JsonProperty("Team Win")
                    String teamWin
            ) {}

            public record Player(
                    @JsonProperty("player_id")
                    String playerId,

                    @JsonProperty("player_stats")
                    PlayerStats playerStats
            ) {
                public record PlayerStats(
                        @JsonProperty("K/D Ratio") String kdRatio,
                        @JsonProperty("ADR") String adr,
                        @JsonProperty("Kills") String kills,
                        @JsonProperty("Deaths") String deaths,
                        @JsonProperty("Assists") String assists,
                        @JsonProperty("Headshots %") String headshotsPercentage,
                        @JsonProperty("Double Kills") String doubleKills,
                        @JsonProperty("Triple Kills") String tripleKills,
                        @JsonProperty("Quadro Kills") String quadroKills,
                        @JsonProperty("Penta Kills") String pentaKills
                ) {}
            }
        }
    }


}
