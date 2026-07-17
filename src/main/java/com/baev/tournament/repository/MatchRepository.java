package com.baev.tournament.repository;

import com.baev.tournament.model.Match;
import java.util.List;

public interface MatchRepository {
    Match findById(Long id);
    Match save(Match match);
    void update(Match match);
    List<Match> findByTournamentId(Long tournamentId);
}