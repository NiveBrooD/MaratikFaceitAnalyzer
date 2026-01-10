package com.ramis.faceit_analyzer.controller;

import com.ramis.faceit_analyzer.model.StatsResponse;
import com.ramis.faceit_analyzer.model.TimeFrame;
import com.ramis.faceit_analyzer.service.FaceitAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/faceit")
@RequiredArgsConstructor
public class FaceitController {

    private final FaceitAnalyzerService faceitAnalyzerService;

    @GetMapping("/yesterday")
    public ResponseEntity<StatsResponse> getFaceitStats() {
        return ResponseEntity.ok(faceitAnalyzerService.getStats(TimeFrame.YESTERDAY_PLAYED));
    }

    @GetMapping("/last_five")
    public ResponseEntity<StatsResponse> getStatsForLastFiveMatches() {
        return ResponseEntity.ok(faceitAnalyzerService.getStats(TimeFrame.LAST_FIVE_PLAYED));
    }
}
