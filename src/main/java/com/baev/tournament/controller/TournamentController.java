package com.baev.tournament.controller;

import com.baev.tournament.model.Match;
import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import com.baev.tournament.service.MatchService;
import com.baev.tournament.service.TournamentService;
import com.baev.tournament.service.UserService;
import com.baev.tournament.model.Tournament;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {
    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final UserService userService;

    public TournamentController(TournamentService tournamentService, MatchService matchService, UserService userService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.userService = userService;
    }

    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    @GetMapping("/{id}")
    public Tournament getTournamentById(@PathVariable Long id) {
        return tournamentService.getTournamentById(id);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Tournament createTournament(@RequestBody Tournament tournament, @RequestParam Long userId) {
        userService.checkRole(userId, Role.ORGANIZER, Role.ADMIN);
        return tournamentService.createTournament(tournament);
    }

    @PutMapping("/{id}")
    public Tournament updateTournament(@PathVariable Long id, @RequestBody Tournament tournament, @RequestParam Long userId) {
        userService.checkRole(userId, Role.ADMIN);
        return tournamentService.updateTournament(id, tournament);
    }

    @DeleteMapping("/{id}")
    public void deleteTournament(@PathVariable Long id, @RequestParam Long userId) {
        userService.checkRole(userId, Role.ADMIN);
        tournamentService.deleteTournament(id);
    }

    @PostMapping("/{tournamentId}/register/{userId}")
    public void registerParticipant(@PathVariable Long tournamentId, @PathVariable Long userId) {
        userService.checkRole(userId, Role.PLAYER);
        tournamentService.registerParticipant(tournamentId, userId);
    }

    @GetMapping("/{id}/participants")
    public List<User> getParticipants(@PathVariable Long id) {
        return tournamentService.getTournamentParticipants(id);
    }

    @PostMapping("/{id}/start")
    public void startTournament(@PathVariable Long id, @RequestParam Long userId) {
        userService.checkRole(userId, Role.ORGANIZER, Role.ADMIN);
        tournamentService.startTournament(id);
    }

    @GetMapping("/{id}/matches")
    public List<Match> getTournamentMatches(@PathVariable Long id) {
        return matchService.getMatchesByTournamentId(id);
    }
}
