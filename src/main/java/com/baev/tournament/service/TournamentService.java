package com.baev.tournament.service;

import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.User;
import java.util.List;

public interface TournamentService {
    Tournament createTournament(Tournament tournament);
    List<Tournament> getAllTournaments();
    Tournament getTournamentById(Long id);
    Tournament updateTournament(Long id, Tournament tournament);
    void deleteTournament(Long id);
    void registerParticipant(Long tournamentId, Long userId);
    List<User> getTournamentParticipants(Long tournamentId);

    void startTournament(Long id);
}