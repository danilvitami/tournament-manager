package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;

import javax.sql.DataSource;// Интерфейс для управления пулом подключений к базе
import org.springframework.stereotype.Repository;
import java.sql.Connection;// Класс физического "провода" к базе данных
import java.sql.PreparedStatement;// Класс для безопасности SQL запросов
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TournamentRepositoryJdbcImpl implements TournamentRepository {

    private final DataSource dataSource;

    public TournamentRepositoryJdbcImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Tournament save(Tournament tournament){
        String sql = "INSERT INTO tournaments (name,description, discipline, min_participants, max_participants, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tournament.getName());
            pstmt.setString(2, tournament.getDescription());
            pstmt.setString(3, tournament.getDiscipline());
            pstmt.setInt(4, tournament.getMinParticipants());
            pstmt.setInt(5, tournament.getMaxParticipants());
            pstmt.setString(6, tournament.getStatus().name());

            pstmt.executeUpdate();
        }
        catch(SQLException e){
            throw new RuntimeException("Ошибка при сохранении турнира" + tournament.getName(),e);
        }
        finally{
            if (pstmt != null){
                try{
                    pstmt.close();
                }
                catch(SQLException e){
                System.err.println("[TournametRepository.save] Не удается закрыть PreparedStatement: "+ e.getMessage());
            }
        }
            if (conn != null){
                try{conn.close();}
                catch (SQLException e){
                    System.err.println("[TournametRepository.save] Не удается закрыть Connection: "+ e.getMessage());
                }
            }
    }
        return tournament;
    }
    @Override
    public List<Tournament> findAll(){
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Tournament t = new Tournament();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("title"));
                t.setDiscipline(rs.getString("discipline"));
                tournaments.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка турниров", e);
        } finally {
            if (rs != null) {
                try { rs.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.findAll] Ошибка закрытия ResultSet: " + e.getMessage()); }
            }
            if (pstmt != null) {
                try { pstmt.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.findAll] Ошибка закрытия PreparedStatement: " + e.getMessage()); }
            }
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.findAll] Ошибка закрытия Connection: " + e.getMessage()); }
            }
        }
        return tournaments;
    }

    @Override
    public Tournament findById(Long id) {
        String sql = "SELECT * FROM tournaments WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Tournament t = new Tournament();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("title"));
                t.setDiscipline(rs.getString("discipline"));
                return t;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске турнира с id: " + id, e);
        } finally {
            if (rs != null) {
                try { rs.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.findById] Ошибка закрытия ResultSet: " + e.getMessage()); }
            }
            if (pstmt != null) {
                try { pstmt.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.findById] Ошибка закрытия PreparedStatement: " + e.getMessage()); }
            }
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.findById] Ошибка закрытия Connection: " + e.getMessage()); }
            }
        }
        return null;
    }
    @Override
    public Tournament update(Tournament tournament) {
        String sql = "UPDATE tournaments SET title = ?, discipline = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, tournament.getName());
            pstmt.setString(2, tournament.getDiscipline());
            pstmt.setLong(3, tournament.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Турнир с id " + tournament.getId() + " не найден в базе.");
            }

            return tournament;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении турнира с id: " + tournament.getId(), e);
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.update] Ошибка закрытия PreparedStatement: " + e.getMessage()); }
            }
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.update] Ошибка закрытия Connection: " + e.getMessage()); }
            }
        }
    }
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении турнира с id: " + id, e);
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.deleteById] Ошибка закрытия PreparedStatement: " + e.getMessage()); }
            }
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.deleteById] Ошибка закрытия Connection: " + e.getMessage()); }
            }
        }
    }
    @Override
    public void addParticipant(Long tournamentId, Long userId) {
        String sql = "INSERT INTO tournament_users (tournament_id, user_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, tournamentId);
            pstmt.setLong(2, userId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при регистрации на турнир. Возможно, пользователь уже зарегистрирован.", e);
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.addParticipant] Ошибка закрытия PreparedStatement: " + e.getMessage()); }
            }
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { System.err.println("[TournamentRepository.addParticipant] Ошибка закрытия Connection: " + e.getMessage()); }
            }
        }
    }
}
