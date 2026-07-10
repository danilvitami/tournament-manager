package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;

public interface TournamentRepository{
    Tournament save(Tournament tournament);
}