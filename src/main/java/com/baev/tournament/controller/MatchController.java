package com.baev.tournament.controller;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.Match;
import com.baev.tournament.service.MatchService;
import com.baev.tournament.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final UserService userService;

    public MatchController(MatchService matchService, UserService userService) {
        this.matchService = matchService;
        this.userService = userService;
    }

    @PostMapping("/{id}/result")
    public void recordResult(
            @PathVariable Long id,
            @RequestParam int score1,
            @RequestParam int score2,
            @RequestParam Long userId) {
        userService.checkRole(userId, Role.ORGANIZER, Role.ADMIN);
        matchService.recordResult(id, score1, score2);
    }

    @GetMapping("/{id}")
    public Match getMatch(@PathVariable Long id) {
        return matchService.getMatchById(id);
    }
}
