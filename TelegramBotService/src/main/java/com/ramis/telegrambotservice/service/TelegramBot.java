package com.ramis.telegrambotservice.service;

import com.ramis.telegrambotservice.dto.NoStatistics;
import com.ramis.telegrambotservice.dto.Request;
import com.ramis.telegrambotservice.dto.StatsResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-name}")
    private String botUsername;

    @Value("${telegram.bot-token}")
    private String botToken;

    private final FaceitService faceitService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            try {
                if (message.startsWith("/get_yesterday_stats")) {
                    getYesterdayStatsCommand(update);
                } else if (message.startsWith("/start")) {
                    startCommand(update);
                } else if (message.startsWith("/last_five_matches")) {
                    getLastFiveMatchesCommand(update);
                } else {
                    echo(update);
                }
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    private void startCommand(Update update) throws TelegramApiException {
        execute(new SendMessage(update.getMessage().getChatId().toString(), "Let's start"));
        log.info("Reply send.");
    }

    private void getYesterdayStatsCommand(Update update) throws TelegramApiException {
        Request statistics = faceitService.getYesterdayStatistics();
        extractDataAndSendMessage(update, statistics);
    }

    private void getLastFiveMatchesCommand(Update update) throws TelegramApiException {
        Request statistics = faceitService.getLastFiveMatchesStatistics();
        extractDataAndSendMessage(update, statistics);
    }

    private void extractDataAndSendMessage(Update update, Request statistics) throws TelegramApiException {
        if (statistics instanceof NoStatistics noStatistics) {
            execute(new SendMessage(update.getMessage().getChatId().toString(), noStatistics.message()));
        } else {
            if (statistics instanceof StatsResponse statsResponse) {
                String statsStr = String.format("""
                                Статистика за %s
                                
                                Игр сыграно: %d
                                Побед: %d
                                Поражений: %d
                                
                                Средний K/D: %.2f
                                Средний ADR: %.1f
                                Средние убийства: %.1f
                                Средние смерти: %.1f
                                Средние ассисты: %.1f
                                Средние хедшоты: %.1f%%
                                
                                Мультикиллы за игру:
                                Double (2x): %.2f
                                Triple (3x): %.2f
                                Quadro (4x): %.2f
                                Penta (5x): %.2f
                                
                                ID матчей:
                                %s
                                """,
                        statsResponse.date(),
                        statsResponse.gamesPlayed(),
                        statsResponse.wins(),
                        statsResponse.loss(),
                        statsResponse.avgKd(),
                        statsResponse.avgAdr(),
                        statsResponse.avgKills(),
                        statsResponse.avgDeaths(),
                        statsResponse.avgAssists(),
                        statsResponse.avgHeadshots(),
                        statsResponse.avgDoubleKills(),
                        statsResponse.avgTripleKills(),
                        statsResponse.avgQuadroKills(),
                        statsResponse.avgPentaKills(),
                        String.join(", \n", statsResponse.matches())
                );
                execute(new SendMessage(update.getMessage().getChatId().toString(), statsStr));
            }
        }
    }

    private void echo(Update update) throws TelegramApiException {
        execute(new SendMessage(update.getMessage().getChatId().toString(), update.getMessage().getText()));
    }
}
