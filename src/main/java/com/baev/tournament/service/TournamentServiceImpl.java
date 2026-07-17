package com.baev.tournament.service;

import com.baev.tournament.model.Match;
import com.baev.tournament.model.User;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.TournamentStatus;
import com.baev.tournament.repository.MatchRepository;
import com.baev.tournament.repository.TournamentRepository;
import com.baev.tournament.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository, MatchRepository matchRepository) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public Tournament createTournament(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().isBlank()) {
            throw new IllegalArgumentException("Ошибка. Название турнира не может быть пустым");
        }
        if (tournament.getMinParticipants() > tournament.getMaxParticipants()) {
            throw new IllegalArgumentException("Ошибка. Мин. количество игроков " + tournament.getMinParticipants() +
                    " не может быть больше макс. " + tournament.getMaxParticipants());
        }

        return tournamentRepository.save(tournament);
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public Tournament updateTournament(Long id, Tournament tournament) {
        Tournament existingTournament = tournamentRepository.findById(id);
        if (existingTournament == null) {
            throw new RuntimeException("Турнир с id " + id + " не найден");
        }
        tournament.setId(id);

        return tournamentRepository.update(tournament);
    }

    @Override
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }

    @Override
    public void registerParticipant(Long tournamentId, Long userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId);
        if (tournament == null) {
            throw new RuntimeException("Невозможно зарегистрироваться. Турнир с id " + tournamentId + " не найден.");
        }

        tournamentRepository.addParticipant(tournamentId, userId);
    }

    @Override
    public List<User> getTournamentParticipants(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId);
        if (tournament == null) {
            throw new RuntimeException("Турнир с id " + tournamentId + " не найден.");
        }
        return userRepository.findUsersByTournamentId(tournamentId);
    }

    @Override
    public void startTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id);
        if (tournament == null) {
            throw new RuntimeException("Турнир с id " + id + " не найден");
        }

        List<User> participants = userRepository.findUsersByTournamentId(id);
        int currentCount = participants != null ? participants.size() : 0;

        if (currentCount < 2 || currentCount < tournament.getMinParticipants()) {
            throw new IllegalStateException("Недостаточно участников для старта турнира. Текущее количество: "
                    + currentCount + ", нужно минимум: " + Math.max(2, tournament.getMinParticipants()));
        }

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentRepository.update(tournament);

        Collections.shuffle(participants);
        int n = participants.size();
        int bracketSize = nextPowerOf2(n);
        int totalRounds = (int) (Math.log(bracketSize) / Math.log(2));

        // base[round] = индекс в списке allMatches, где начинается этот раунд
        int[] base = new int[totalRounds + 1];
        base[0] = 0;
        for (int r = 1; r <= totalRounds; r++) {
            base[r] = base[r - 1] + bracketSize / (int) Math.pow(2, r);
        }

        // Создаём все матчи и сохраняем в БД, чтобы получить id
        List<Match> allMatches = new ArrayList<>();
        for (int round = 0; round < totalRounds; round++) {
            int matchesInRound = bracketSize / (int) Math.pow(2, round + 1);
            for (int pos = 0; pos < matchesInRound; pos++) {
                Match m = new Match();
                m.setTournamentId(id);
                m.setStage(getStageName(round, totalRounds));
                m = matchRepository.save(m);
                allMatches.add(m);
            }
        }

        // Связываем матчи- победитель матча в раунде N попадает в матч раунда N+1
        for (int round = 0; round < totalRounds - 1; round++) {
            int matchesInRound = bracketSize / (int) Math.pow(2, round + 1);
            for (int pos = 0; pos < matchesInRound; pos++) {
                Match current = allMatches.get(base[round] + pos);
                Match nextRoundMatch = allMatches.get(base[round + 1] + pos / 2);
                current.setNextMatchId(nextRoundMatch.getId());
                matchRepository.update(current);
            }
        }

        // Расставляем участников в первый раунд
        int firstRoundMatches = bracketSize / 2;
        int byes = bracketSize - n; // кол-во "выходов" — матчей с одним игроком

        int playerIdx = 0;
        for (int pos = 0; pos < firstRoundMatches; pos++) {
            Match m = allMatches.get(pos);
            if (pos < byes) {
                // Bye: один игрок, автоматически проходит дальше
                m.setPlayer1Id(participants.get(playerIdx).getId());
                playerIdx++;
                m.setWinnerId(m.getPlayer1Id());
                matchRepository.update(m);

                // Автопереход в следующий раунд
                if (m.getNextMatchId() != null) {
                    Match next = matchRepository.findById(m.getNextMatchId());
                    if (next != null) {
                        if (next.getPlayer1Id() == null) {
                            next.setPlayer1Id(m.getWinnerId());
                        } else {
                            next.setPlayer2Id(m.getWinnerId());
                        }
                        matchRepository.update(next);
                    }
                }
            } else {
                // Обычный матч: два игрока
                m.setPlayer1Id(participants.get(playerIdx).getId());
                playerIdx++;
                m.setPlayer2Id(participants.get(playerIdx).getId());
                playerIdx++;
                matchRepository.update(m);
            }
        }
    }

    // Ближайшая степень двойки >= n
    private int nextPowerOf2(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    // Название стадии по номеру раунда
    private String getStageName(int round, int totalRounds) {
        int fromFinal = totalRounds - 1 - round;
        return switch (fromFinal) {
            case 0 -> "FINAL";
            case 1 -> "SEMI_FINAL";
            case 2 -> "QUARTER_FINAL";
            default -> "ROUND_" + (round + 1);
        };
    }
}
