package com.baev.tournament.service;

import com.baev.tournament.model.Tournament;
import java.util.List;

public interface TournamentService {
    Tournament createTournament(Tournament tournament);

    List<Tournament> getAllTournaments();
    Tournament getTournamentById(Long id);
}