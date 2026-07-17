package com.baev.tournament.controller;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import com.baev.tournament.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user, @RequestParam Long userId) {
        userService.checkRole(userId, Role.ADMIN);
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username, @RequestParam Long userId) {
        userService.checkRole(userId, Role.ADMIN);
        userService.deleteUser(username);
    }
}
