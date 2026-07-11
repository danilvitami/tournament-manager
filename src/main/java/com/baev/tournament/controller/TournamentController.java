package com.baev.tournament.controller;

import com.baev.tournament.service.TournamentService;
import com.baev.tournament.model.Tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/tournament")

public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/create")

    @ResponseStatus(HttpStatus.CREATED)

    public Tournament createTournament(@RequestBody Tournament tournament){
        return tournamentService.createTournament(tournament);
    }
    @PutMapping("/{id}")
    public Tournament updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        return tournamentService.updateTournament(id, tournament);
    }
    @DeleteMapping("/{id}")
    public void deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
    }
}