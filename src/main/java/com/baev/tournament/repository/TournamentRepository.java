package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;

import java.util.List;

public interface TournamentRepository{
    Tournament save(Tournament tournament);
    List<Tournament> findAll();
    Tournament findById(Long id);
    Tournament update (Tournament tournament);
    void deleteById(Long id);
    void addParticipant(Long tournamentId, Long userId);
}