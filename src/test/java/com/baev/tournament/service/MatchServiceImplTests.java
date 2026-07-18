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

import java.util.List;

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
    void recordMatchResult_WhenDraw_ThrowsException() {
        Tournament t = tournamentService.createTournament(new Tournament("MatchTour3", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("Player7", "pass", "p7@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("Player8", "pass", "p8@mail.com", Role.PLAYER));

        Match match = new Match();
        match.setTournamentId(t.getId());
        match.setPlayer1Id(p1.getId());
        match.setPlayer2Id(p2.getId());
        Match savedMatch = matchRepository.save(match);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            matchService.recordResult(savedMatch.getId(), 3, 3);
        });
    }

    @Test
    void recordMatchResult_WhenMatchDoesNotExist_ThrowsNotFoundException() {
        Assertions.assertThrows(com.baev.tournament.exception.NotFoundException.class, () -> {
            matchService.recordResult(99999L, 3, 1);
        });
    }

    @Test
    void recordMatchResult_AdvancesWinnerToNextMatch() {
        Tournament t = tournamentService.createTournament(new Tournament("AdvTour", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("AdvP1", "pass", "adv1@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("AdvP2", "pass", "adv2@mail.com", Role.PLAYER));

        Match semi = new Match();
        semi.setTournamentId(t.getId());
        semi.setPlayer1Id(p1.getId());
        semi.setPlayer2Id(p2.getId());
        semi.setStage("SEMI_FINAL");
        Match savedSemi = matchRepository.save(semi);

        Match finalMatch = new Match();
        finalMatch.setTournamentId(t.getId());
        finalMatch.setStage("FINAL");
        Match savedFinal = matchRepository.save(finalMatch);

        savedSemi.setNextMatchId(savedFinal.getId());
        matchRepository.update(savedSemi);

        matchService.recordResult(savedSemi.getId(), 16, 10);

        Match updatedFinal = matchRepository.findById(savedFinal.getId());
        Assertions.assertEquals(p1.getId(), updatedFinal.getPlayer1Id(), "Победитель должен попасть в финал");
    }

    @Test
    void recordMatchResult_OnFinal_CompletesTournament() {
        Tournament t = tournamentService.createTournament(new Tournament("FinTour", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("FinP1", "pass", "fin1@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("FinP2", "pass", "fin2@mail.com", Role.PLAYER));

        Match finalMatch = new Match();
        finalMatch.setTournamentId(t.getId());
        finalMatch.setPlayer1Id(p1.getId());
        finalMatch.setPlayer2Id(p2.getId());
        finalMatch.setStage("FINAL");
        Match savedFinal = matchRepository.save(finalMatch);

        matchService.recordResult(savedFinal.getId(), 16, 14);

        Tournament completed = tournamentService.getTournamentById(t.getId());
        Assertions.assertEquals(com.baev.tournament.model.TournamentStatus.COMPLETED, completed.getStatus());
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

    @Test
    void getMatchById_WhenMatchDoesNotExist_ThrowsNotFoundException() {
        Assertions.assertThrows(com.baev.tournament.exception.NotFoundException.class, () -> {
            matchService.getMatchById(99999L);
        });
    }

    @Test
    void getMatchesByTournamentId_WhenMatchesExist_ReturnsList() {
        Tournament t = tournamentService.createTournament(new Tournament("ListTour", "Desc", "Chess", 2, 8));
        User p1 = userService.registerUser(new User("ListP1", "pass", "lp1@mail.com", Role.PLAYER));
        User p2 = userService.registerUser(new User("ListP2", "pass", "lp2@mail.com", Role.PLAYER));

        Match match1 = new Match();
        match1.setTournamentId(t.getId());
        match1.setPlayer1Id(p1.getId());
        match1.setPlayer2Id(p2.getId());
        matchRepository.save(match1);

        Match match2 = new Match();
        match2.setTournamentId(t.getId());
        match2.setPlayer1Id(p1.getId());
        match2.setPlayer2Id(p2.getId());
        matchRepository.save(match2);

        List<Match> matches = matchService.getMatchesByTournamentId(t.getId());

        Assertions.assertEquals(2, matches.size());
    }

    @Test
    void getMatchesByTournamentId_WhenNoMatches_ReturnsEmptyList() {
        Tournament t = tournamentService.createTournament(new Tournament("EmptyTour", "Desc", "Chess", 2, 8));

        List<Match> matches = matchService.getMatchesByTournamentId(t.getId());

        Assertions.assertTrue(matches.isEmpty());
    }
}