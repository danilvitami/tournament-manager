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

    @PostMapping("/create")//Определяет тип запроса и его путь:
    // POST используется для создания новых данных
    // URL для этого метода будет: .../api/tournaments/create
    @ResponseStatus(HttpStatus.CREATED)// задаем конкретный HTTP-код состояния

    public Tournament createTournament(@RequestBody Tournament tournament){
        return tournamentService.createTournament(tournament);
    }
}