package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest
class TournamentRepositoryJdbcImplTests {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE tournament_users, matches, tournaments, users CASCADE");
    }

    @Test
    void save_WhenAllFieldsAreOk_ReturnsTournamentWithId() {
        Tournament tournament = new Tournament("Test Tournament", "Desc", "Chess", 2, 8);

        Tournament saved = tournamentRepository.save(tournament);

        Assertions.assertNotNull(saved.getId(), "ID не должен быть null");
        Assertions.assertEquals("Test Tournament", saved.getName());
    }

    @Test
    void findById_WhenTournamentExists_ReturnsTournament() {
        Tournament saved = tournamentRepository.save(new Tournament("FindByIdTour", "Desc", "Chess", 2, 8));

        Tournament found = tournamentRepository.findById(saved.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("FindByIdTour", found.getName());
        Assertions.assertEquals("Chess", found.getDiscipline());
        Assertions.assertEquals(2, found.getMinParticipants());
        Assertions.assertEquals(8, found.getMaxParticipants());
    }

    @Test
    void findById_WhenTournamentDoesNotExist_ReturnsNull() {
        Tournament found = tournamentRepository.findById(99999L);

        Assertions.assertNull(found, "Должен вернуться null для несуществующего турнира");
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

    @Test
    void update_WhenStatusChanges_UpdatesStatus() {
        Tournament tournament = tournamentRepository.save(new Tournament("StatusTour", "Desc", "Chess", 2, 8));
        tournament.setStatus(com.baev.tournament.model.TournamentStatus.IN_PROGRESS);

        tournamentRepository.update(tournament);
        Tournament found = tournamentRepository.findById(tournament.getId());

        Assertions.assertEquals(com.baev.tournament.model.TournamentStatus.IN_PROGRESS, found.getStatus());
    }

    @Test
    void deleteById_WhenTournamentExists_DeletesSuccessfully() {
        Tournament saved = tournamentRepository.save(new Tournament("DeleteTour", "Desc", "Chess", 2, 8));

        tournamentRepository.deleteById(saved.getId());

        Tournament found = tournamentRepository.findById(saved.getId());
        Assertions.assertNull(found, "Турнир должен быть удален");
    }

    @Test
    void addParticipant_WhenTournamentAndUserExist_AddsParticipant() {
        Tournament saved = tournamentRepository.save(new Tournament("PartTour", "Desc", "Chess", 2, 8));

        jdbcTemplate.update("INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)",
                "PartUser", "pass", "part@mail.com", "PLAYER");
        Long userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'PartUser'", Long.class);

        tournamentRepository.addParticipant(saved.getId(), userId);

        List<com.baev.tournament.model.User> participants = jdbcTemplate.query(
                "SELECT u.* FROM users u JOIN tournament_users tu ON u.id = tu.user_id WHERE tu.tournament_id = ?",
                new Object[]{saved.getId()},
                (rs, rowNum) -> {
                    com.baev.tournament.model.User u = new com.baev.tournament.model.User();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    return u;
                });
        Assertions.assertEquals(1, participants.size());
    }
}