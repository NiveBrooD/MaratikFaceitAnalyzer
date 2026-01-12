package com.ramis.faceit_analyzer.controller;

import com.ramis.faceit_analyzer.model.StatsResponse;
import com.ramis.faceit_analyzer.model.TimeFrame;
import com.ramis.faceit_analyzer.service.FaceitAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/faceit/statistics")
@RequiredArgsConstructor
public class FaceitController {

    private final FaceitAnalyzerService faceitAnalyzerService;

    @GetMapping("/days/yesterday")
    public ResponseEntity<StatsResponse> getFaceitStats() {
        return ResponseEntity.ok(faceitAnalyzerService.getStats(TimeFrame.YESTERDAY_PLAYED));
    }

    @GetMapping("/days/{date}")
    public ResponseEntity<StatsResponse> getFaceitStats(@PathVariable("date") String date) {
        return ResponseEntity.ok(faceitAnalyzerService.getStats(date));
    }


    @GetMapping("/matches/last_five")
    public ResponseEntity<StatsResponse> getStatsForLastFiveMatches() {
        return ResponseEntity.ok(faceitAnalyzerService.getStats(TimeFrame.LAST_FIVE_PLAYED));
    }
}
