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
public class UserRepositoryJdbcImpl implements UserRepository {

    private final DataSource dataSource;

    public UserRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole().name());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                }
            }
            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    return user;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске пользователя по id: " + id, e);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    return user;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске пользователя", e);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя: " + username, e);
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole().name());
            pstmt.setLong(5, user.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Пользователь с id " + user.getId() + " не найден.");
            }
            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя с id: " + user.getId(), e);
        }
    }

    @Override
    public List<User> findUsersByTournamentId(Long tournamentId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN tournament_users tu ON u.id = tu.user_id " +
                "WHERE tu.tournament_id = ?";

        List<User> participants = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tournamentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    participants.add(user);
                }
            }
            return participants;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении участников турнира с id: " + tournamentId, e);
        }
    }
}