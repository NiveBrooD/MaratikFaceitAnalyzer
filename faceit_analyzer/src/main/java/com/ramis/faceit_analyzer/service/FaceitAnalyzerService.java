package com.ramis.faceit_analyzer.service;

import com.ramis.faceit_analyzer.config.FaceitProperties;
import com.ramis.faceit_analyzer.exception.MaratikNotFoundException;
import com.ramis.faceit_analyzer.exception.MaratikNotPlayedYesterday;
import com.ramis.faceit_analyzer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class FaceitAnalyzerService {

    private final FaceitProperties faceitProperties;
    private final RestClient restClient;

    public FaceitHistory getYesterdayMatches() {
        return restClient.get()
                .uri("/players/" + faceitProperties.getId() + "/history")
                .retrieve().toEntity(FaceitHistory.class)
                .getBody();
    }

    public List<Match> filterMatches(FaceitHistory faceitHistory) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow")).minusDays(1);
        List<Match> todayMatches = new CopyOnWriteArrayList<>();
        faceitHistory.getMatches().stream().parallel().forEach((match -> {
            Long finishedAt = match.getFinishedAt();
            LocalDateTime matchDate = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(finishedAt), ZoneId.of("Europe/Moscow")
            );
            if (now.getYear() == matchDate.getYear()
                && now.getMonth() == matchDate.getMonth()
                && now.getDayOfMonth() == matchDate.getDayOfMonth()) {
                todayMatches.add(match);
            }
        }));
        if (todayMatches.isEmpty()) {
            throw new MaratikNotPlayedYesterday("Maratik has not been played yesterday");
        }
        return todayMatches;
    }

    public StatsResponse summaryAllStats() {
        List<Match> matches = filterMatches(getYesterdayMatches());
        List<MatchStatistic> matchStatistics = new CopyOnWriteArrayList<>();
        matches.stream().parallel().forEach(match -> {
            MatchResponse matchResponse = restClient.get()
                    .uri("/matches/" + match.getMatchId() + "/stats")
                    .retrieve()
                    .toEntity(MatchResponse.class)
                    .getBody();
            matchStatistics.add(findMaratAndGetHisStats(matchResponse));
        });
        return new StatsResponse(
                matchStatistics.size(),
                findWins(matchStatistics),
                matchStatistics.size() - findWins(matchStatistics),
                findAvgKd(matchStatistics),
                findAvgAdr(matchStatistics),
                findAvgKills(matchStatistics),
                findAvgDeaths(matchStatistics),
                findAvgAssists(matchStatistics),
                findAvgHs(matchStatistics),
                findAvgDoubleKills(matchStatistics),
                findAvgTripleKills(matchStatistics),
                findAvgQuadroKills(matchStatistics),
                findAvgPentaKills(matchStatistics),
                matchStatistics.stream().map(MatchStatistic::getMatchId).toList()
        );
    }

    private Double findAvgPentaKills(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getPentaKills)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgQuadroKills(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getQuadroKills)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgTripleKills(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getTripleKills)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgDoubleKills(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getDoubleKills)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgHs(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getHeadshotsPercentage)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgAssists(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getAssists)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgDeaths(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getDeaths)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgKills(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getKills)
                .mapToDouble(Double::valueOf)
                .average().orElse(0.0);
    }

    private Double findAvgAdr(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getAdr)
                .mapToDouble(Double::doubleValue)
                .average().orElse(0.0);
    }

    private Double findAvgKd(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getKdRatio)
                .mapToDouble(Double::doubleValue)
                .average().orElse(0.0);
    }

    private Integer findWins(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getWinning)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private MatchStatistic findMaratAndGetHisStats(MatchResponse matchResponse) {
        return matchResponse.rounds()
                .stream()
                .flatMap(round -> round.teams().stream()
                        .flatMap(team -> team.players().stream()
                                .filter(player -> faceitProperties.getId().equals(player.playerId()))
                                .map(player -> {
                                    int win = Integer.parseInt(team.teamStats().teamWin());
                                    return new MatchStatistic(
                                            round.matchId(),
                                            win,
                                            Double.parseDouble(player.playerStats().kdRatio()),
                                            Double.parseDouble(player.playerStats().adr()),
                                            Integer.parseInt(player.playerStats().kills()),
                                            Integer.parseInt(player.playerStats().deaths()),
                                            Integer.parseInt(player.playerStats().assists()),
                                            Integer.parseInt(player.playerStats().headshotsPercentage()),
                                            Integer.parseInt(player.playerStats().doubleKills()),
                                            Integer.parseInt(player.playerStats().tripleKills()),
                                            Integer.parseInt(player.playerStats().quadroKills()),
                                            Integer.parseInt(player.playerStats().pentaKills())
                                    );
                                })
                        )
                ).findFirst()
                .orElseThrow(() -> new MaratikNotFoundException("Maratik not found"));
    }
}
