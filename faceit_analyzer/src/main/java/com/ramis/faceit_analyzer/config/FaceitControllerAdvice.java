package com.ramis.faceit_analyzer.config;

import com.ramis.faceit_analyzer.exception.MaratikNotFoundException;
import com.ramis.faceit_analyzer.exception.MaratikNotPlayedYesterday;
import com.ramis.faceit_analyzer.model.NoStatistic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FaceitControllerAdvice {

    @ExceptionHandler(MaratikNotPlayedYesterday.class)
    public ResponseEntity<NoStatistic> handleMaratikNotPlayedException(MaratikNotPlayedYesterday e) {
        return ResponseEntity.ok(new NoStatistic(e.getMessage()));
    }

    @ExceptionHandler(MaratikNotFoundException.class)
    public ResponseEntity<NoStatistic> handleMaratikNotFoundException(MaratikNotFoundException e) {
        return ResponseEntity.ok(new NoStatistic(e.getMessage()));
    }
}
