package com.baev.tournament.controller;

import com.baev.tournament.model.Match;
import com.baev.tournament.service.MatchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/{id}/result")
    public void recordResult(
            @PathVariable Long id,
            @RequestParam int score1,
            @RequestParam int score2) {

        matchService.recordResult(id, score1, score2);
    }

    @GetMapping("/{id}")
    public Match getMatch(@PathVariable Long id) {
        return matchService.getMatchById(id);
    }
}