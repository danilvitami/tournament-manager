package com.baev.tournament.repository;

import com.baev.tournament.model.Match;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchRepositoryJdbcImpl implements MatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public MatchRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Match> matchRowMapper = (rs, rowNum) -> new Match(
            rs.getLong("id"),
            rs.getLong("tournament_id"),
            rs.getLong("player1_id"),
            rs.getLong("player2_id"),
            (Integer) rs.getObject("score1"),
            (Integer) rs.getObject("score2"),
            (Long) rs.getObject("winner_id")
    );

    @Override
    public Match findById(Long id) {
        String sql = "SELECT * FROM matches WHERE id = ?";
        List<Match> results = jdbcTemplate.query(sql, matchRowMapper, id);

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public Match save(Match match) {
        String sql = "INSERT INTO matches (tournament_id, player1_id, player2_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, match.getTournamentId(), match.getPlayer1Id(), match.getPlayer2Id());
        return match;
    }

    @Override
    public void update(Match match) {
        String sql = "UPDATE matches SET score1 = ?, score2 = ?, winner_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, match.getScore1(), match.getScore2(), match.getWinnerId(), match.getId());
    }

    @Override
    public List<Match> findByTournamentId(Long tournamentId) {
        String sql = "SELECT * FROM matches WHERE tournament_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, tournamentId);
    }
}