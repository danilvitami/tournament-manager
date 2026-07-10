package com.baev.tournament.repository;

import com.baev.tournament.model.Tournament;

import javax.sql.DataSource;// Интерфейс для управления пулом подключений к базе
import org.springframework.stereotype.Repository;
import java.sql.Connection;// Класс физического "провода" к базе данных
import java.sql.PreparedStatement;// Класс для безопасности SQL запросов
import java.sql.SQLException;

@Repository
public class TournamentRepositoryJdbcImpl implements TournamentRepository {

    private final DataSource dataSource;

    public TournamentRepositoryJdbcImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public void save(Tournament tournament){
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
                System.err.println("[TournametRepository.save] Не удается заакрыть PreparedStatement: "+ e.getMessage());
            }
        }
            if (conn != null){
                try{conn.close();}
                catch (SQLException e){
                    System.err.println("[TournametRepository.save] Не удается заакрыть Connection: "+ e.getMessage());
                }
            }
    }
}}
