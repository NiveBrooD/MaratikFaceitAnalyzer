package com.ramis.telegrambotservice.service;

import com.ramis.telegrambotservice.dto.NoStatistics;
import com.ramis.telegrambotservice.dto.Request;
import com.ramis.telegrambotservice.dto.StatsResponse;
import com.ramis.telegrambotservice.model.Chat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

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
    private final ChatService chatService;

    @Scheduled(cron = "0 30 9 * * *", zone = "Europe/Moscow")
    public void scheduledYesterdayStatisticsSender() {
        Request yesterdayStatistics = faceitService.getYesterdayStatistics();
        List<Chat> allChats = chatService.getAllChats();
        allChats.forEach(chat -> {
            try {
                extractDataAndSendMessage(chat.getId(), yesterdayStatistics);
                Thread.sleep(1000);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }

        });
    }

    @Override
    public void onUpdateReceived(Update update) {
        chatService.saveOrUpdate(update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            try {
                if (message.startsWith("/get_yesterday_stats")) {
                    getYesterdayStatsCommand(update);
                } else if (message.startsWith("/start")) {
                    startCommand(update);
                } else if (message.startsWith("/last_five_matches")) {
                    getLastFiveMatchesCommand(update);
                } else if (message.startsWith("/date")) {
                    getStatsByDateCommand(update);
                } else {
                    echo(update);
                }
            } catch (TelegramApiException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    private void getStatsByDateCommand(Update update) throws TelegramApiException {
        String text = update.getMessage().getText();
        String[] split = text.split(" ");
        if (split.length == 1) {
            sendDateErrorMessage(update);
            return;
        }
        String[] split1 = split[1].split("\\.");
        if (split1.length != 3) {
            sendDateErrorMessage(update);
            return;
        }
        String formattedStr = checkAndReturnString(split1, update);
        Request statisticForDate = faceitService.getStatisticForDate(formattedStr);
        extractDataAndSendMessage(update.getMessage().getChatId(), statisticForDate);
    }

    private String checkAndReturnString(String[] date, Update update) throws TelegramApiException {
        String day = date[0];
        String month = date[1];
        String year = date[2];

        try {
            int dayInt = Integer.parseInt(day);
            int monthInt = Integer.parseInt(month);
            int yearInt = Integer.parseInt(year);

            if (dayInt > 31 || dayInt < 1
                               && monthInt > 12 || monthInt < 1
                                                   && yearInt > 2050 || yearInt < 2000) {
                sendDateErrorMessage(update);
            }
        } catch (NumberFormatException e) {
            sendDateErrorMessage(update);
        }
        return year + "-" + month + "-" + fixDayFormat(day);
    }

    private String fixDayFormat(String day) {
        if (Integer.parseInt(day) < 10 && !day.startsWith("0")) {
            return "0" + day;
        } else  {
            return day;
        }
    }

    private void sendDateErrorMessage(Update update) throws TelegramApiException {
        execute(new SendMessage(update.getMessage().getChatId().toString(),
                "Добавьте дату в форме -> /date [ДАТА в формате 11.01.2026]")
        );
    }

    private void startCommand(Update update) throws TelegramApiException {
        execute(new SendMessage(update.getMessage().getChatId().toString(), "Let's start"));
        log.info("Reply send.");
    }

    private void getYesterdayStatsCommand(Update update) throws TelegramApiException {
        Request statistics = faceitService.getYesterdayStatistics();
        extractDataAndSendMessage(update.getMessage().getChatId(), statistics);
    }

    private void getLastFiveMatchesCommand(Update update) throws TelegramApiException {
        Request statistics = faceitService.getLastFiveMatchesStatistics();
        extractDataAndSendMessage(update.getMessage().getChatId(), statistics);
    }

    private void extractDataAndSendMessage(Long chatId, Request statistics) throws TelegramApiException {
        if (statistics instanceof NoStatistics noStatistics) {
            execute(new SendMessage(chatId.toString(), noStatistics.message()));
        } else {
            if (statistics instanceof StatsResponse statsResponse) {
                String statsStr = String.format("""
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
                execute(new SendMessage(chatId.toString(), statsStr));
            }
        }
    }

    private void echo(Update update) throws TelegramApiException {
        execute(new SendMessage(update.getMessage().getChatId().toString(), update.getMessage().getText()));
    }
}
