package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;

public interface TournamentRepository{
    void save(Tournament tournament);
}