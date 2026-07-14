package com.baev.tournament.service;

import com.baev.tournament.model.User;

public interface UserService {
    User registerUser(User user);
    User getUserByUsername(String username);
    void deleteUser(String username);
}