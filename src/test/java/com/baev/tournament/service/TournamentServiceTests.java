package com.baev.tournament.service;

import com.baev.tournament.model.Tournament;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TournamentServiceTests {

    @Autowired
    private TournamentService tournamentService;

    @Test
    void createTournament_WhenAllFieldsAreOk_SavesTournament() {
        // Given
        Tournament tournament = new Tournament("Кубок", "Описание", "Футбол", 2, 10);

        // When
        Tournament saved = tournamentService.createTournament(tournament);

        // Then
        Assertions.assertNotNull(saved.getId());
    }

    @Test
    void createTournament_WhenNameIsEmpty_ThrowsException() {
        Tournament tournament = new Tournament("", "Описание", "Футбол", 2, 10);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.createTournament(tournament);
        });
    }

    @Test
    void getTournamentById_WhenTournamentExists_ReturnsTournament() {
        Tournament saved = tournamentService.createTournament(new Tournament("Кубок", "Оп", "Ф", 2, 2));

        Tournament found = tournamentService.getTournamentById(saved.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("Кубок", found.getName());
    }
}