package com.baev.tournament.service;

import com.baev.tournament.model.Match;
import com.baev.tournament.model.Role;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.User;
import com.baev.tournament.repository.MatchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class MatchServiceImplTests {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE tournament_users, matches, tournaments, users CASCADE");
    }

    @Test
    void recordMatchResult_WhenPlayer1ScoreIsHigher_SetsPlayer1AsWinner() {
        Tournament t = tournamentService.createTournament(new Tournament("MatchTour", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("Player3", "pass", "p3@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("Player4", "pass", "p4@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());
        Match savedMatch = matchRepository.save(match);

        matchService.recordResult(savedMatch.getId(), 3, 1);

        Match updatedMatch = matchRepository.findById(savedMatch.getId());
        Assertions.assertEquals(3, updatedMatch.getScore1());
        Assertions.assertEquals(p1.getId(), updatedMatch.getWinnerId());
    }

    @Test
    void recordMatchResult_WhenPlayer2ScoreIsHigher_SetsPlayer2AsWinner() {
        Tournament t = tournamentService.createTournament(new Tournament("MatchTour2", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("Player5", "pass", "p5@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("Player6", "pass", "p6@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());
        Match savedMatch = matchRepository.save(match);


        matchService.recordResult(savedMatch.getId(), 2, 5);

        Match updatedMatch = matchRepository.findById(savedMatch.getId());
        Assertions.assertEquals(p2.getId(), updatedMatch.getWinnerId());
    }
    @Test
    void getMatchById_WhenMatchExists_ReturnsMatch() {
        Tournament t = tournamentService.createTournament(new Tournament("MatchTour4", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("Player9", "pass", "p9@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("Player10", "pass", "p10@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());
        Match savedMatch = matchRepository.save(match);

        Match foundMatch = matchService.getMatchById(savedMatch.getId());


        Assertions.assertNotNull(foundMatch);
        Assertions.assertEquals(savedMatch.getId(), foundMatch.getId());
    }
}