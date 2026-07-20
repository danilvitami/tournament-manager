package com.baev.tournament.service;

import com.baev.tournament.exception.NotFoundException;
import com.baev.tournament.model.Match;
import com.baev.tournament.model.TournamentStatus;
import com.baev.tournament.repository.MatchRepository;
import com.baev.tournament.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public MatchServiceImpl(MatchRepository matchRepository, TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
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

        // Если есть следующий матч - автопереход победителя
        if (match.getNextMatchId() != null) {
            Match nextMatch = matchRepository.findById(match.getNextMatchId());
            if (nextMatch != null) {
                if (nextMatch.getPlayer1Id() == null) {
                    nextMatch.setPlayer1Id(match.getWinnerId());
                } else {
                    nextMatch.setPlayer2Id(match.getWinnerId());
                }
                matchRepository.update(nextMatch);
            }
        } else {
            // Финальный матч - турнир завершён
            var tournament = tournamentRepository.findById(match.getTournamentId());
            if (tournament != null) {
                tournament.setStatus(TournamentStatus.COMPLETED);
                tournamentRepository.update(tournament);
            }
        }
    }

    @Override
    public Match getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId);
        if (match == null) {
            throw new NotFoundException("Матч с id " + matchId + " не найден");
        }
        return match;
    }

    @Override
    public List<Match> getMatchesByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }
}
