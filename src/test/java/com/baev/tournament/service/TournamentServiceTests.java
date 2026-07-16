package com.baev.tournament.service;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.TournamentStatus;
import com.baev.tournament.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class TournamentServiceTests {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE tournament_users, matches, tournaments, users CASCADE");
    }

    @Test
    void createTournament_WhenAllFieldsAreOk_SavesTournament() {

        Tournament tournament = new Tournament("Кубок", "Описание", "Футбол", 2, 10);

        Tournament saved = tournamentService.createTournament(tournament);

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

        Tournament saved = tournamentService.createTournament(new Tournament("Летний Кубок", "Оп", "Ф", 2, 2));

        Tournament found = tournamentService.getTournamentById(saved.getId());

        Assertions.assertNotNull(found);
    }

    @Test
    void startTournament_WhenAllOk_UpdatesStatus() {

        Tournament tournament = tournamentService.createTournament(new Tournament("Тест Старт", "Оп", "Шахматы", 2, 10));
        User p1 = userService.registerUser(new User("Player1", "pass", "p1@mail.ru", Role.PLAYER));
        User p2 = userService.registerUser(new User("Player2", "pass", "p2@mail.ru", Role.PLAYER));

        tournamentService.registerParticipant(tournament.getId(), p1.getId());
        tournamentService.registerParticipant(tournament.getId(), p2.getId());


        tournamentService.startTournament(tournament.getId());

        Tournament started = tournamentService.getTournamentById(tournament.getId());
        Assertions.assertEquals(TournamentStatus.IN_PROGRESS, started.getStatus());
    }
    @Test
    void startTournament_WhenNotEnoughPlayers_ThrowsException() {

        Tournament tournament = tournamentService.createTournament(new Tournament("Тест Лимит", "Оп", "Шахматы", 4, 10));
        User p1 = userService.registerUser(new User("LoneWolf", "pass", "lone@mail.ru", Role.PLAYER));

        tournamentService.registerParticipant(tournament.getId(), p1.getId());

        Assertions.assertThrows(IllegalStateException.class, () -> {
            tournamentService.startTournament(tournament.getId());
        });
    }
}