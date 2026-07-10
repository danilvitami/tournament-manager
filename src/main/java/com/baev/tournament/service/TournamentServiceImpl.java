package com.baev.tournament.service;

import com.baev.tournament.repository.TournamentRepository;
import com.baev.tournament.model.Tournament;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository){
        this.tournamentRepository = tournamentRepository;
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

}
