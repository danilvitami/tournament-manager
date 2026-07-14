package com.baev.tournament.service;

import com.baev.tournament.model.User;
import com.baev.tournament.repository.UserRepository;
import org.springframework.stereotype.Service;

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
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь " + username + " не найден");
        }
        return user;
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }
}