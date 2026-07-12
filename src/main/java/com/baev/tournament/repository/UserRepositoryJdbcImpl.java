package com.baev.tournament.repository;

import com.baev.tournament.model.User;
import com.baev.tournament.model.Role;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;// Интерфейс для управления пулом подключений к базе
import java.sql.Connection;// Класс физического "провода" к базе данных
import java.sql.PreparedStatement;// Класс для безопасности SQL запросов
import java.sql.ResultSet;// Класс для хранения ответа от базы,то есть строк из таблицы
import java.sql.SQLException;
@Repository
public class UserRepositoryJdbcImpl implements UserRepository{

    private final DataSource dataSource ;

    public UserRepositoryJdbcImpl (DataSource dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public User save(User user){
        String sql = "INSERT INTO users (username, password,email, role) VALUES (?, ?, ?, ?) RETURNING id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();//берем готовый канал связи с БД
            pstmt = conn.prepareStatement(sql);// Передаем текст в БД и создаем безопасный шаблон запроса
            pstmt.setString(1,user.getUsername());
            pstmt.setString(2,user.getPassword());
            pstmt.setString(3,user.getEmail());
            pstmt.setString(4,user.getRole().name());

            rs = pstmt.executeQuery();//база выполняет запрос и возвращает resultSet(таблицу)

            if (rs.next()){
                user.setId(rs.getLong("id"));//если база вернула строку
                // с новым ID, извлекаем его из "id" и записываем в объект
            }
            return user;//пользователь с обновленным ID из бд
        }
        catch(SQLException e){
            throw new RuntimeException("Ошибка при сохранении пользователя",e);
        }
        finally{
            if (rs != null) {
                try{rs.close();}
                catch (SQLException e){
                    System.err.println("[UserRepository.save] Не удается закрыть ResultSet:" + e.getMessage());
                }
            }
            if (pstmt  != null){
                try{
                    pstmt.close();
                }
                catch (SQLException e){
                    System.err.println("[UserRepository.save] Не удается закрыть PreparedStatment:" + e.getMessage());
                }
            }
            if (conn != null){
                try{
                    conn.close();
                }
                catch(SQLException e){
                    System.err.println("[UserRepository.save] Не удается закрыть Connection:"+ e.getMessage());
                }
            }
        }
    }
    @Override
    public User findByUsername(String username){
        String sql = "SELECT * FROM users WHERE username = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(Role.valueOf(rs.getString("role")));
                return user;
            }
            return null;
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске пользователя",e);
        }
        finally{
            if (rs != null){
                try {rs.close();}
                catch (SQLException e){
                    System.err.println("[UserRepository.findByUsername] Не удается закрыть ResultSet:" + e.getMessage());
                }
            }
            if (pstmt != null){
                try{pstmt.close();}
                catch (SQLException e){
                    System.err.println("[UserRepository.findByUsername] Не удается закрыть PreparedStatement:" + e.getMessage());
                }
            }
            if (conn != null){
                try{conn.close();}
                catch(SQLException e){
                    System.err.println("[UserRepository.findByUsername] Не удается закрыть Connection:");
                }
            }
        }
    }
    @Override
    public void deleteByUsername(String username){
        String sql = "DELETE FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            throw new RuntimeException("Ошибка при удалении пользователя:"+ username,e);
        }
        finally{
            if(pstmt != null){
                try{
                    pstmt.close();
                }
                catch (SQLException e){
                    System.err.println("[UserRepository.delete] Не удается закрыть PreparedStatement: " + e.getMessage());
                }
            }
            if (conn != null){
                try{
                    conn.close();
                }
                catch(SQLException e){
                    System.err.println("[UserRepository.delete] Не удается закрыть Connection: " + e.getMessage());
                }
            }
        }
    }
    @Override
    public List<User> findUsersByTournamentId(Long tournamentId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN tournament_users tu ON u.id = tu.user_id " +
                "WHERE tu.tournament_id = ?";

        List<User> participants = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, tournamentId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));

                // user.setRole(Role.valueOf(rs.getString("role")));

                participants.add(user);
            }

            return participants;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении участников турнира с id: " + tournamentId, e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { System.err.println("Ошибка закрытия ResultSet"); }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { System.err.println("Ошибка закрытия PreparedStatement"); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { System.err.println("Ошибка закрытия Connection"); }
            }
        }
    }
}
