package com.baev.tournament.service;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;
    @BeforeEach
    void setUp() {
        try {
            userService.deleteUser("NewUser");
            userService.deleteUser("Bilbo");
        } catch (Exception e) {
        }
    }
    @Test
    void registerUser_WhenUserIsValid_ReturnsUserWithId() {
        // Given
        User user = new User("NewUser", "password", "new@mail.com", Role.PLAYER);

        // When
        User saved = userService.registerUser(user);

        // Then
        Assertions.assertNotNull(saved.getId(), "Пользователь должен быть сохранен с ID");
    }

    @Test
    void getUserByUsername_WhenUserExists_ReturnsUser() {
        userService.registerUser(new User("Bilbo", "pass", "bilbo@mail.com", Role.PLAYER));

        User found = userService.getUserByUsername("Bilbo");

        Assertions.assertNotNull(found);
        Assertions.assertEquals("Bilbo", found.getUsername());
    }
}