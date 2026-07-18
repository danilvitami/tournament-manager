package com.baev.tournament.repository;

import com.baev.tournament.model.Match;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MatchRepositoryJdbcImpl implements MatchRepository {

    private final DataSource dataSource;

    public MatchRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Match save(Match match) {
        String sql = "INSERT INTO matches (tournament_id, player1_id, player2_id, stage, next_match_id) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

до            pstmt.setObject(1, match.getTournamentId());
            pstmt.setObject(2, match.getPlayer1Id());
            pstmt.setObject(3, match.getPlayer2Id());
            pstmt.setString(4, match.getStage());
            if (match.getNextMatchId() != null) {
                pstmt.setLong(5, match.getNextMatchId());
            } else {
                pstmt.setNull(5, java.sql.Types.BIGINT);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    match.setId(rs.getLong("id"));
                }
            }
            return match;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении матча", e);
        }
    }

    @Override
    public Match findById(Long id) {
        String sql = "SELECT * FROM matches WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMatch(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске матча по id: " + id, e);
        }
    }

    @Override
    public void update(Match match) {
        String sql = "UPDATE matches SET player1_id = ?, player2_id = ?, score1 = ?, score2 = ?, winner_id = ?, stage = ?, next_match_id = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, match.getPlayer1Id());
            pstmt.setObject(2, match.getPlayer2Id());
            pstmt.setObject(3, match.getScore1());
            pstmt.setObject(4, match.getScore2());
            pstmt.setObject(5, match.getWinnerId());
            pstmt.setString(6, match.getStage());
            if (match.getNextMatchId() != null) {
                pstmt.setLong(7, match.getNextMatchId());
            } else {
                pstmt.setNull(7, java.sql.Types.BIGINT);
            }
            pstmt.setLong(8, match.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении матча", e);
        }
    }

    @Override
    public List<Match> findByTournamentId(Long tournamentId) {
        String sql = "SELECT * FROM matches WHERE tournament_id = ?";
        List<Match> matches = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tournamentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(mapRowToMatch(rs));
                }
            }
            return matches;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении матчей турнира с id: " + tournamentId, e);
        }
    }

    private Match mapRowToMatch(ResultSet rs) throws SQLException {
        return new Match(
                rs.getLong("id"),
                rs.getLong("tournament_id"),
                (Long) rs.getObject("player1_id"),
                (Long) rs.getObject("player2_id"),
                (Integer) rs.getObject("score1"),
                (Integer) rs.getObject("score2"),
                (Long) rs.getObject("winner_id"),
                rs.getString("stage"),
                (Long) rs.getObject("next_match_id")
        );
    }
}