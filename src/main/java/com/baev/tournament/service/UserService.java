package com.baev.tournament.service;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;

public interface UserService {
    User registerUser(User user);
    User findById(Long id);
    User getUserByUsername(String username);
    User updateUser(Long id, User user);
    void deleteUser(String username);
    void checkRole(Long userId, Role... allowedRoles);
}