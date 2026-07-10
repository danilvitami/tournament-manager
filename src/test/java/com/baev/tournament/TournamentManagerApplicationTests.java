package com.baev.tournament;

import com.baev.tournament.repository.UserRepository;
import com.baev.tournament.repository.TournamentRepository;
import com.baev.tournament.model.User;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.Role;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Assertions;
import java.util.List;

@SpringBootTest
class TournamentManagerApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testUserOperations(){
        /// 1)deleteByUsername
        System.out.println("Очистка данных пользователя");
        userRepository.deleteByUsername("Tom");
        System.out.println("База от Tom очищена");


        System.out.println("Очистка данных пользователя");
        userRepository.deleteByUsername("Bob");
        System.out.println("База от Bob очищена");

        /// 2) Save для User
        System.out.println("Тест метода save() для пользователя");
        User Tom = new User("Tom","Secret07", "Tom07@xmail.ru", Role.PLAYER);
        User savedTom = userRepository.save(Tom);
        System.out.println("сохранил пользователя Tom");
        // Проверка что БД вернула пользователя с назначенным ID (не null)
        Assertions.assertNotNull(savedTom.getId(), "ID пользователя Tom не должен быть null после сохранения");

        User Bob = new User();
        Bob.setUsername("Bob");
        Bob.setPassword("aBc2233");
        Bob.setEmail("Bob9@gmail.com");
        Bob.setRole(Role.PLAYER);
        userRepository.save(Bob);
        System.out.println("Сохранил пользвателя Bob");

        /// 3) findByUsername
        System.out.println("Тест метода findByUsername");
        User foundPers = userRepository.findByUsername("Tom");

        Assertions.assertNotNull(foundPers, "Пользователь Tom должен быть найден в БД");
        Assertions.assertEquals("Tom", foundPers.getUsername(), "Имена должны совпадать");
        Assertions.assertEquals(Role.PLAYER, foundPers.getRole(), "Роли должны совпадать");
    }
        @Test
        void testTournamentOperations(){
       /// для турниров
        // 1. Сохранение турнира
        System.out.println("Тест метода save() для турнира");
        Tournament tour_1 = new Tournament("Кубок Саратова", "Для любителей", "Шахматы",10, 32);
        Tournament savedTour = tournamentRepository.save(tour_1);
        Assertions.assertNotNull(savedTour.getId(), "ID турнира не должен быть null");
        System.out.println("Турнир добавлен");
        // 2. Тест метода findAll
        Tournament foundTour = tournamentRepository.findById(savedTour.getId());
        Assertions.assertNotNull(foundTour, "Турнир должен быть найден по ID");
        Assertions.assertEquals("Кубок Саратова", foundTour.getName(), "Названия турниров должны совпадать");
        // 3. Тест метода findAll
        List<Tournament> allTournaments = tournamentRepository.findAll();
        Assertions.assertFalse(allTournaments.isEmpty(), "Список турниров не должен быть пустым");

        boolean isOurTournamentInList = allTournaments.stream().anyMatch(t -> t.getName().equals("Кубок Саратова"));
        Assertions.assertTrue(isOurTournamentInList, "Созданный турнир должен присутствовать в общем списке");
    }
}
