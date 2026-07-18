package com.baev.tournament.repository;

import com.baev.tournament.model.Match;
import com.baev.tournament.model.Role;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.User;
import com.baev.tournament.service.TournamentService;
import com.baev.tournament.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest
public class MatchRepositoryJdbcImplTests {

    @Autowired
    private MatchRepository matchRepository;

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
    void save_WhenMatchIsValid_ReturnsMatchWithId() {
        Tournament t = tournamentService.createTournament(new Tournament("RepoTour", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("RepoPlayer1", "pass", "rp1@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("RepoPlayer2", "pass", "rp2@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());

        Match savedMatch = matchRepository.save(match);

        Assertions.assertNotNull(savedMatch.getId(), "ID матча должен быть сгенерирован");
        Assertions.assertEquals(t.getId(), savedMatch.getTournamentId());
    }

    @Test
    void save_WhenMatchHasStage_SavesStage() {
        Tournament t = tournamentService.createTournament(new Tournament("StageTour", "Desc", "Chess", 2, 8));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setStage("SEMI_FINAL");

        Match saved = matchRepository.save(match);

        Match found = matchRepository.findById(saved.getId());
        Assertions.assertEquals("SEMI_FINAL", found.getStage());
    }

    @Test
    void save_WhenMatchHasNextMatchId_SavesNextMatchId() {
        Tournament t = tournamentService.createTournament(new Tournament("NextTour", "Desc", "Chess", 2, 8));

        Match finalMatch = new Match();
        finalMatch.setTournamentId(t.getId());
        finalMatch.setStage("FINAL");
        Match savedFinal = matchRepository.save(finalMatch);

        Match semiMatch = new Match();
        semiMatch.setTournamentId(t.getId());
        semiMatch.setStage("SEMI_FINAL");
        semiMatch.setNextMatchId(savedFinal.getId());
        Match savedSemi = matchRepository.save(semiMatch);

        Match found = matchRepository.findById(savedSemi.getId());
        Assertions.assertEquals(savedFinal.getId(), found.getNextMatchId());
    }

    @Test
    void save_WhenNullableFieldsAreNull_SavesSuccessfully() {
        Tournament t = tournamentService.createTournament(new Tournament("NullTour", "Desc", "Chess", 2, 8));

        Match match = new Match();
        match.setTournamentId(t.getId());

        Match saved = matchRepository.save(match);

        Match found = matchRepository.findById(saved.getId());
        Assertions.assertNull(found.getPlayer1Id(), "player1Id должен быть null");
        Assertions.assertNull(found.getPlayer2Id(), "player2Id должен быть null");
        Assertions.assertNull(found.getScore1(), "score1 должен быть null");
        Assertions.assertNull(found.getScore2(), "score2 должен быть null");
        Assertions.assertNull(found.getWinnerId(), "winnerId должен быть null");
    }

    @Test
    void findById_WhenMatchExists_ReturnsMatch() {
        Tournament t = tournamentService.createTournament(new Tournament("RepoTour2", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("RepoPlayer3", "pass", "rp3@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("RepoPlayer4", "pass", "rp4@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());
        Match savedMatch = matchRepository.save(match);

        Match foundMatch = matchRepository.findById(savedMatch.getId());

        Assertions.assertNotNull(foundMatch);
        Assertions.assertEquals(savedMatch.getId(), foundMatch.getId());
    }

    @Test
    void findById_WhenMatchDoesNotExist_ReturnsNull() {
        Match foundMatch = matchRepository.findById(99999L);

        Assertions.assertNull(foundMatch, "Должен вернуться null для несуществующего матча");
    }

    @Test
    void update_WhenMatchExists_UpdatesScoresAndWinner() {
        Tournament t = tournamentService.createTournament(new Tournament("RepoTour3", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("RepoPlayer5", "pass", "rp5@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("RepoPlayer6", "pass", "rp6@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());
        Match savedMatch = matchRepository.save(match);

        savedMatch.setScore1(5);
        savedMatch.setScore2(2);
        savedMatch.setWinnerId(p1.getId());

        matchRepository.update(savedMatch);

        Match updatedMatch = matchRepository.findById(savedMatch.getId());
        Assertions.assertEquals(5, updatedMatch.getScore1());
        Assertions.assertEquals(2, updatedMatch.getScore2());
        Assertions.assertEquals(p1.getId(), updatedMatch.getWinnerId());
    }

    @Test
    void update_WhenSettingStageAndNextMatchId_UpdatesSuccessfully() {
        Tournament t = tournamentService.createTournament(new Tournament("UpdTour", "Desc", "Chess", 2, 8));

        Match finalMatch = new Match();
        finalMatch.setTournamentId(t.getId());
        finalMatch.setStage("FINAL");
        Match savedFinal = matchRepository.save(finalMatch);

        Match semiMatch = new Match();
        semiMatch.setTournamentId(t.getId());
        Match savedSemi = matchRepository.save(semiMatch);

        savedSemi.setStage("SEMI_FINAL");
        savedSemi.setNextMatchId(savedFinal.getId());
        matchRepository.update(savedSemi);

        Match found = matchRepository.findById(savedSemi.getId());
        Assertions.assertEquals("SEMI_FINAL", found.getStage());
        Assertions.assertEquals(savedFinal.getId(), found.getNextMatchId());
    }

    @Test
    void findByTournamentId_WhenMatchesExist_ReturnsList() {
        Tournament t = tournamentService.createTournament(new Tournament("RepoTour4", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("RepoPlayer7", "pass", "rp7@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("RepoPlayer8", "pass", "rp8@mail.com", Role.PLAYER));

        Match match1 = new Match();
        match1.setTournamentId(t.getId());
        match1.setPlayer1Id(p1.getId());
        match1.setPlayer2Id(p2.getId());
        matchRepository.save(match1);

        List<Match> matches = matchRepository.findByTournamentId(t.getId());

        Assertions.assertFalse(matches.isEmpty());
        Assertions.assertEquals(1, matches.size());
    }

    @Test
    void findByTournamentId_WhenNoMatches_ReturnsEmptyList() {
        Tournament t = tournamentService.createTournament(new Tournament("EmptyTour", "Desc", "Chess", 2, 8));

        List<Match> matches = matchRepository.findByTournamentId(t.getId());

        Assertions.assertTrue(matches.isEmpty());
    }
}