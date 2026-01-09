package com.ramis.faceit_analyzer.service;

import com.ramis.faceit_analyzer.config.FaceitProperties;
import com.ramis.faceit_analyzer.exception.MaratikNotFoundException;
import com.ramis.faceit_analyzer.exception.MaratikNotPlayedYesterday;
import com.ramis.faceit_analyzer.model.FaceitHistory;
import com.ramis.faceit_analyzer.model.MatchFullStatistics;
import com.ramis.faceit_analyzer.model.MatchStatistic;
import com.ramis.faceit_analyzer.model.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaceitAnalyzerService {

    private final FaceitProperties faceitProperties;
    private final RestClient restClient;
    private final KafkaProducerService kafkaProducerService;

    public FaceitHistory getYesterdayMatches() {
        return restClient.get()
                .uri("/players/" + faceitProperties.getId() + "/history")
                .retrieve().toEntity(FaceitHistory.class)
                .getBody();
    }


    public List<FaceitHistory.Match> filterMatches(FaceitHistory faceitHistory) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow")).minusDays(1);
        List<FaceitHistory.Match> todayMatches = new CopyOnWriteArrayList<>();
        faceitHistory.matches().stream().parallel().forEach((match -> {
            Long finishedAt = match.finishedAt();
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
        List<FaceitHistory.Match> matches = filterMatches(getYesterdayMatches());
        List<MatchStatistic> matchStatistics = matches.parallelStream()
                .map(match -> {
                    MatchFullStatistics matchFullStatistics = restClient.get()
                            .uri("/matches/" + match.matchId() + "/stats")
                            .retrieve()
                            .toEntity(MatchFullStatistics.class)
                            .getBody();
                    return findMaratAndGetHisStats(Objects.requireNonNull(matchFullStatistics));
                }).collect(Collectors.toList());

        StatsResponse statsResponse = new StatsResponse(
                matchStatistics.size(),
                findWins(matchStatistics),
                matchStatistics.size() - findWins(matchStatistics),
                LocalDate.now(),
                findAvg(matchStatistics, MatchStatistic::getKdRatio),
                findAvg(matchStatistics, MatchStatistic::getAdr),
                findAvg(matchStatistics, MatchStatistic::getKills),
                findAvg(matchStatistics, MatchStatistic::getDeaths),
                findAvg(matchStatistics, MatchStatistic::getAssists),
                findAvg(matchStatistics, MatchStatistic::getHeadshotsPercentage),
                findAvg(matchStatistics, MatchStatistic::getDoubleKills),
                findAvg(matchStatistics, MatchStatistic::getTripleKills),
                findAvg(matchStatistics, MatchStatistic::getQuadroKills),
                findAvg(matchStatistics, MatchStatistic::getPentaKills),
                matchStatistics.stream().map(MatchStatistic::getMatchId).toList()
        );

        kafkaProducerService.sendStats(statsResponse);
        return statsResponse;
    }

    private Double findAvg(List<MatchStatistic> matchStatistic, ToDoubleFunction<MatchStatistic> mapper) {
        return matchStatistic.stream()
                .mapToDouble(mapper)
                .average()
                .orElse(0.0);
    }

    private Integer findWins(List<MatchStatistic> matchStatistics) {
        return matchStatistics.stream()
                .map(MatchStatistic::getWinning)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private MatchStatistic findMaratAndGetHisStats(MatchFullStatistics matchFullStatistics) {
        return matchFullStatistics.rounds()
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
