package com.baev.tournament.service;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import com.baev.tournament.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь " + username + " не найден");
        }
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Пользователь с id " + id + " не найден");
        }
        user.setId(id);
        return userRepository.update(user);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public void checkRole(Long userId, Role... allowedRoles) {
        User user = findById(userId);
        boolean hasRole = Arrays.asList(allowedRoles).contains(user.getRole());
        if (!hasRole) {
            throw new SecurityException("Пользователь " + user.getUsername()
                    + " не имеет прав. Требуется роль: " + Arrays.toString(allowedRoles)
                    + ", текущая роль: " + user.getRole());
        }
    }
}