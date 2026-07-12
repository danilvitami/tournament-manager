package com.baev.tournament.service;

import com.baev.tournament.model.User;
import com.baev.tournament.repository.TournamentRepository;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
    }
    @Override
    public Tournament createTournament(Tournament tournament){

        if (tournament.getName() == null || tournament.getName().isBlank()){//.trim().isEmpty()
            throw new IllegalArgumentException("Ошибка. Название турнира не может быть пустым");
        }
        if (tournament.getMinParticipants() > tournament.getMaxParticipants()){
            throw new IllegalArgumentException("Ошибка. Мин. количество игнроков "+ tournament.getMinParticipants()+
            "не может быть больше макс. " + tournament.getMaxParticipants());
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

}
