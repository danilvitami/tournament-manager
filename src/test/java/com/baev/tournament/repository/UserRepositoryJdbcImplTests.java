package com.baev.tournament.repository;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryJdbcImplTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteByUsername("TestUser");
    }

    @Test
    void save_WhenAllFieldsAreOk_ReturnsUserWithId() {
        //Given
        User newUser = new User("TestUser", "pass123", "test@mail.com", Role.PLAYER);

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        Assertions.assertNotNull(savedUser.getId(), "ID не должен быть null после сохранения");
        Assertions.assertEquals("TestUser", savedUser.getUsername(), "Имя пользователя должно совпадать");
    }

    @Test
    void findByUsername_WhenUserExists_ReturnsUser() {

        User existingUser = new User("TestUser", "pass123", "test@mail.com", Role.PLAYER);
        userRepository.save(existingUser);

        User foundUser = userRepository.findByUsername("TestUser");

        Assertions.assertNotNull(foundUser, "Пользователь должен быть найден");
        Assertions.assertEquals("test@mail.com", foundUser.getEmail(), "Email должен совпадать");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ReturnsNull() {

        User foundUser = userRepository.findByUsername("TestUser");

        Assertions.assertNull(foundUser, "Должен вернуться null, если пользователя нет в базе");
    }
}