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

import java.util.List;

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
    void createTournament_WhenMinGreaterThanMax_ThrowsException() {
        Tournament tournament = new Tournament("БагТурнир", "Оп", "Футбол", 10, 2);

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
    void getTournamentById_WhenTournamentDoesNotExist_ReturnsNull() {
        Tournament found = tournamentService.getTournamentById(99999L);

        Assertions.assertNull(found, "Должен вернуться null для несуществующего турнира");
    }

    @Test
    void getAllTournaments_WhenTournamentsExist_ReturnsList() {
        tournamentService.createTournament(new Tournament("Тур1", "Оп", "Шахматы", 2, 8));
        tournamentService.createTournament(new Tournament("Тур2", "Оп", "Шахматы", 2, 8));

        List<Tournament> tournaments = tournamentService.getAllTournaments();

        Assertions.assertFalse(tournaments.isEmpty());
        Assertions.assertTrue(tournaments.size() >= 2);
    }

    @Test
    void updateTournament_WhenTournamentExists_UpdatesFields() {
        Tournament saved = tournamentService.createTournament(new Tournament("Старое", "Оп", "Футбол", 2, 8));
        Tournament newData = new Tournament("Новое", "Новая оп", "Баскетбол", 4, 16);

        Tournament updated = tournamentService.updateTournament(saved.getId(), newData);

        Assertions.assertEquals("Новое", updated.getName());
        Assertions.assertEquals("Баскетбол", updated.getDiscipline());
    }

    @Test
    void updateTournament_WhenTournamentDoesNotExist_ThrowsException() {
        Tournament newData = new Tournament("Фантом", "Оп", "Футбол", 2, 8);

        Assertions.assertThrows(RuntimeException.class, () -> {
            tournamentService.updateTournament(99999L, newData);
        });
    }

    @Test
    void deleteTournament_WhenTournamentExists_DeletesSuccessfully() {
        Tournament saved = tournamentService.createTournament(new Tournament("Удаляемый", "Оп", "Футбол", 2, 8));

        tournamentService.deleteTournament(saved.getId());

        Tournament found = tournamentService.getTournamentById(saved.getId());
        Assertions.assertNull(found, "Турнир должен быть удален");
    }

    @Test
    void registerParticipant_WhenTournamentAndUserExist_AddsParticipant() {
        Tournament tournament = tournamentService.createTournament(new Tournament("ТурРег", "Оп", "Футбол", 2, 8));
        User player = userService.registerUser(new User("RegPlayer", "pass", "reg@mail.com", Role.PLAYER));

        tournamentService.registerParticipant(tournament.getId(), player.getId());

        List<User> participants = tournamentService.getTournamentParticipants(tournament.getId());
        Assertions.assertEquals(1, participants.size());
        Assertions.assertEquals("RegPlayer", participants.get(0).getUsername());
    }

    @Test
    void getTournamentParticipants_WhenParticipantsExist_ReturnsList() {
        Tournament tournament = tournamentService.createTournament(new Tournament("ТурУчаст", "Оп", "Футбол", 2, 8));
        User p1 = userService.registerUser(new User("Part1", "pass", "p1@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("Part2", "pass", "p2@mail.com", Role.PLAYER));
        tournamentService.registerParticipant(tournament.getId(), p1.getId());
        tournamentService.registerParticipant(tournament.getId(), p2.getId());

        List<User> participants = tournamentService.getTournamentParticipants(tournament.getId());

        Assertions.assertEquals(2, participants.size());
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