package com.baev.tournament.repository;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest
class UserRepositoryJdbcImplTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE tournament_users, matches, tournaments, users CASCADE");
    }

    @Test
    void save_WhenAllFieldsAreOk_ReturnsUserWithId() {
        User newUser = new User("TestUser", "pass123", "test@mail.com", Role.PLAYER);

        User savedUser = userRepository.save(newUser);

        Assertions.assertNotNull(savedUser.getId(), "ID не должен быть null после сохранения");
        Assertions.assertEquals("TestUser", savedUser.getUsername(), "Имя пользователя должно совпадать");
    }

    @Test
    void findById_WhenUserExists_ReturnsUser() {
        User saved = userRepository.save(new User("FindByIdUser", "pass", "fb@mail.com", Role.PLAYER));

        User found = userRepository.findById(saved.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("FindByIdUser", found.getUsername());
        Assertions.assertEquals("fb@mail.com", found.getEmail());
        Assertions.assertEquals(Role.PLAYER, found.getRole());
    }

    @Test
    void findById_WhenUserDoesNotExist_ReturnsNull() {
        User found = userRepository.findById(99999L);

        Assertions.assertNull(found, "Должен вернуться null для несуществующего пользователя");
    }

    @Test
    void findByUsername_WhenUserExists_ReturnsUser() {
        User existingUser = new User("TestUser", "pass123", "test@mail.com", Role.PLAYER);
        userRepository.save(existingUser);

        User foundUser = userRepository.findByUsername("TestUser");

        Assertions.assertNotNull(foundUser, "Пользователь должен быть найден");
        Assertions.assertEquals("test@mail.com", foundUser.getEmail(), "Email должен совпадать");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ReturnsNull() {
        User foundUser = userRepository.findByUsername("TestUser");

        Assertions.assertNull(foundUser, "Должен вернуться null, если пользователя нет в базе");
    }

    @Test
    void update_WhenUserExists_UpdatesFields() {
        User saved = userRepository.save(new User("UpdUser", "pass", "upd@mail.com", Role.PLAYER));
        saved.setUsername("UpdUserNew");
        saved.setEmail("newupd@mail.com");
        saved.setRole(Role.ORGANIZER);

        userRepository.update(saved);

        User found = userRepository.findById(saved.getId());
        Assertions.assertEquals("UpdUserNew", found.getUsername());
        Assertions.assertEquals("newupd@mail.com", found.getEmail());
        Assertions.assertEquals(Role.ORGANIZER, found.getRole());
    }

    @Test
    void deleteByUsername_WhenUserExists_DeletesSuccessfully() {
        userRepository.save(new User("DelUser", "pass", "del@mail.com", Role.PLAYER));

        userRepository.deleteByUsername("DelUser");

        User found = userRepository.findByUsername("DelUser");
        Assertions.assertNull(found, "Пользователь должен быть удален");
    }

    @Test
    void findUsersByTournamentId_WhenParticipantsExist_ReturnsList() {
        userRepository.save(new User("TourP1", "pass", "tp1@mail.com", Role.PLAYER));
        userRepository.save(new User("TourP2", "pass", "tp2@mail.com", Role.PLAYER));

        jdbcTemplate.update("INSERT INTO tournaments (name, discipline, min_participants, max_participants, status) VALUES (?, ?, ?, ?, ?)",
                "TestTournament", "Chess", 2, 8, "REGISTRATION");
        Long tournamentId = jdbcTemplate.queryForObject("SELECT id FROM tournaments WHERE name = 'TestTournament'", Long.class);

        Long p1Id = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'TourP1'", Long.class);
        Long p2Id = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'TourP2'", Long.class);

        jdbcTemplate.update("INSERT INTO tournament_users (tournament_id, user_id) VALUES (?, ?)", tournamentId, p1Id);
        jdbcTemplate.update("INSERT INTO tournament_users (tournament_id, user_id) VALUES (?, ?)", tournamentId, p2Id);

        List<User> participants = userRepository.findUsersByTournamentId(tournamentId);

        Assertions.assertEquals(2, participants.size());
    }
}