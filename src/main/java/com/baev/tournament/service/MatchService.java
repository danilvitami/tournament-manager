package com.baev.tournament.service;

import com.baev.tournament.model.Match;

public interface MatchService {
    void recordResult(Long matchId, int score1, int score2);
    Match getMatchById(Long matchId);
}