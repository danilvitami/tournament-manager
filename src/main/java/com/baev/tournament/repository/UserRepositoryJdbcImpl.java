package com.baev.tournament.repository;

import com.baev.tournament.model.User;
import com.baev.tournament.model.Role;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;// Интерфейс для управления пулом подключений к базе
import java.sql.Connection;// Класс самого физического "провода" к базе данных
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
        //RETURNING id для возврата сгенерированного id новой строки
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        /// поменять на try() {} потом
        try {
            conn = dataSource.getConnection();//берем готовый канал связи с БД
            pstmt = conn.prepareStatement(sql);// Передаем текст в БД и создаем безопасный шаблон запроса
            pstmt.setString(1,user.getUsername());
            pstmt.setString(2,user.getPassword());
            pstmt.setString(3,user.getEmail());
            pstmt.setString(4,user.getRole().name());

            rs = pstmt.executeQuery();//база выполняет запрос и возвращает resultSet

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
                    System.err.println("[UserRepository.save] Не удается закрыть ResultSet:" + e.getMessage());//На случай,если
                    //метод close() тоже выбросит ошибку,в противном случае stmt и conn останутся открытми
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
}
