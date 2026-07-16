package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TournamentRepositoryJdbcImplTests {

    @Autowired
    private TournamentRepository tournamentRepository;
    @BeforeEach
    void setUp() {

        List<Tournament> all = tournamentRepository.findAll();
        for (Tournament t : all) {
            if (t.getName().equals("Test Tournament")) {
                tournamentRepository.deleteById(t.getId());
            }
        }
    }

    @Test
    void save_WhenAllFieldsAreOk_ReturnsTournamentWithId() {
        // Given
        Tournament tournament = new Tournament("Test Tournament", "Desc", "Chess", 2, 8);

        // When
        Tournament saved = tournamentRepository.save(tournament);

        // Then
        Assertions.assertNotNull(saved.getId(), "ID не должен быть null");
        Assertions.assertEquals("Test Tournament", saved.getName());
    }

    @Test
    void findAll_WhenTournamentsExist_ReturnsList() {
        tournamentRepository.save(new Tournament("Test Tournament", "Desc", "Chess", 2, 8));

        List<Tournament> tournaments = tournamentRepository.findAll();

        Assertions.assertFalse(tournaments.isEmpty(), "Список не должен быть пустым");
    }

    @Test
    void update_WhenTournamentExists_UpdatesSuccessfully() {

        Tournament tournament = tournamentRepository.save(new Tournament("Test Tournament", "Desc", "Chess", 2, 8));
        tournament.setName("Updated Tournament");

        tournamentRepository.update(tournament);
        Tournament found = tournamentRepository.findById(tournament.getId());

        Assertions.assertEquals("Updated Tournament", found.getName());
    }
}