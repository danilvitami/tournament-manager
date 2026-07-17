package com.baev.tournament.service;

import com.baev.tournament.exception.NotFoundException;
import com.baev.tournament.model.Match;
import com.baev.tournament.repository.MatchRepository;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public void recordResult(Long matchId, int score1, int score2) {
        Match match = matchRepository.findById(matchId);

        if (match == null) {
            throw new NotFoundException("Матч с id " + matchId + " не найден");
        }

        match.setScore1(score1);
        match.setScore2(score2);

        if (score1 > score2) {
            match.setWinnerId(match.getPlayer1Id());
        } else if (score2 > score1) {
            match.setWinnerId(match.getPlayer2Id());
        } else {
            throw new IllegalArgumentException("Ничья в данном турнире невозможна");
        }

        matchRepository.update(match);
    }

    @Override
    public Match getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId);
        if (match == null) {
            throw new NotFoundException("Матч с id " + matchId + " не найден");
        }
        return match;
    }
}