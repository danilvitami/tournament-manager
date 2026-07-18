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
            userService.deleteUser("UpdateUser");
            userService.deleteUser("DeleteUser");
        } catch (Exception e) {
        }
    }

    @Test
    void registerUser_WhenUserIsValid_ReturnsUserWithId() {
        User user = new User("NewUser", "password", "new@mail.com", Role.PLAYER);

        User saved = userService.registerUser(user);

        Assertions.assertNotNull(saved.getId(), "Пользователь должен быть сохранен с ID");
    }

    @Test
    void getUserByUsername_WhenUserExists_ReturnsUser() {
        userService.registerUser(new User("Bilbo", "pass", "bilbo@mail.com", Role.PLAYER));

        User found = userService.getUserByUsername("Bilbo");

        Assertions.assertNotNull(found);
        Assertions.assertEquals("Bilbo", found.getUsername());
    }

    @Test
    void getUserByUsername_WhenUserDoesNotExist_ThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("NonExistent");
        });
    }

    @Test
    void findById_WhenUserExists_ReturnsUser() {
        User saved = userService.registerUser(new User("NewUser", "pass", "new@mail.com", Role.PLAYER));

        User found = userService.findById(saved.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("NewUser", found.getUsername());
    }

    @Test
    void findById_WhenUserDoesNotExist_ThrowsException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.findById(99999L);
        });
    }

    @Test
    void updateUser_WhenUserExists_UpdatesFields() {
        User saved = userService.registerUser(new User("UpdateUser", "pass", "upd@mail.com", Role.PLAYER));
        User updatedData = new User("UpdateUserNew", "newpass", "newupd@mail.com", Role.ORGANIZER);

        User result = userService.updateUser(saved.getId(), updatedData);

        Assertions.assertEquals("UpdateUserNew", result.getUsername());
        Assertions.assertEquals("newupd@mail.com", result.getEmail());
        Assertions.assertEquals(Role.ORGANIZER, result.getRole());
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ThrowsException() {
        User updatedData = new User("Ghost", "pass", "ghost@mail.com", Role.PLAYER);

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.updateUser(99999L, updatedData);
        });
    }

    @Test
    void deleteUser_WhenUserExists_DeletesSuccessfully() {
        userService.registerUser(new User("DeleteUser", "pass", "del@mail.com", Role.PLAYER));

        userService.deleteUser("DeleteUser");

        Assertions.assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("DeleteUser");
        }, "Пользователь должен быть удален");
    }

    @Test
    void checkRole_WhenUserHasAllowedRole_DoesNotThrow() {
        User saved = userService.registerUser(new User("NewUser", "pass", "new@mail.com", Role.PLAYER));

        Assertions.assertDoesNotThrow(() -> {
            userService.checkRole(saved.getId(), Role.PLAYER, Role.ADMIN);
        });
    }

    @Test
    void checkRole_WhenUserDoesNotHaveRole_ThrowsSecurityException() {
        User saved = userService.registerUser(new User("NewUser", "pass", "new@mail.com", Role.PLAYER));

        Assertions.assertThrows(SecurityException.class, () -> {
            userService.checkRole(saved.getId(), Role.ADMIN);
        });
    }
}