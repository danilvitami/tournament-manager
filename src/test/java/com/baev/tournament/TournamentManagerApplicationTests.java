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
        //Для пользователя
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
       // Для турниров
        /// 1) Сохранение турнира
        System.out.println("Тест метода save() для турнира");
        Tournament tour_1 = new Tournament("Кубок Саратова", "Для любителей", "Шахматы",10, 32);
        Tournament savedTour = tournamentRepository.save(tour_1);
        Assertions.assertNotNull(savedTour.getId(), "ID турнира не должен быть null");
        System.out.println("Турнир добавлен");
        /// 2) Тест метода findAll
        Tournament foundTour = tournamentRepository.findById(savedTour.getId());
        Assertions.assertNotNull(foundTour, "Турнир должен быть найден по ID");
        Assertions.assertEquals("Кубок Саратова", foundTour.getName(), "Названия турниров должны совпадать");
        /// 3) Тест метода findAll
        List<Tournament> allTournaments = tournamentRepository.findAll();
        Assertions.assertFalse(allTournaments.isEmpty(), "Список турниров не должен быть пустым");

        boolean isOurTournamentInList = allTournaments.stream().anyMatch(t -> t.getName().equals("Кубок Саратова"));
        Assertions.assertTrue(isOurTournamentInList, "Созданный турнир должен присутствовать в общем списке");
    }

    @Test
    void testTournamentUpdateAndDelete() {
        System.out.println("Тест методов update() и deleteById() для турниров");

        /// 1)создаем турнир
        Tournament tempTour = new Tournament("Летний Кубок", "Для тестов", "CS:GO", 5, 10);
        Tournament savedTour = tournamentRepository.save(tempTour);
        Long id = savedTour.getId();
        System.out.println("Создан временный турнир с ID: " + id);

        /// 2) Тест метода update
        System.out.println("Тест метода update() для турнира");
        savedTour.setName("Осенний Кубок"); // Меняем название
        savedTour.setDiscipline("Dota 2");   // Меняем дисциплину

        Tournament updatedTour = tournamentRepository.update(savedTour);
        Assertions.assertEquals("Осенний Кубок", updatedTour.getName(), "Название турнира должно было обновиться");
        Assertions.assertEquals("Dota 2", updatedTour.getDiscipline(), "Дисциплина должна была обновиться");
        System.out.println("Турнир успешно обновлен");

        /// 3) Тест метода deleteById
        System.out.println("Тест метода deleteById() для турнира");
        tournamentRepository.deleteById(id);

        Tournament deletedTour = tournamentRepository.findById(id);
        Assertions.assertNull(deletedTour, "Турнир должен быть удален из БД и возвращать null");
        System.out.println("Турнир успешно удален");
    }

    @Test
    void testManyToManyRelations() {
        System.out.println("Тест связей (Регистрация на турнир и получение списка)");

        System.out.println("Очистка данных тестового пользователя Alice");
        userRepository.deleteByUsername("Alice");

        /// 2)Создаем пользователя
        User alice = new User("Alice", "pass123", "alice@mail.ru", Role.PLAYER);
        User savedAlice = userRepository.save(alice);
        System.out.println("Сохранен пользователь Alice");

        /// 3) Создаем турнир
        Tournament tour = new Tournament("Гранд Финал", "Тест связей", "FIFA", 2, 8);
        Tournament savedTour = tournamentRepository.save(tour);
        System.out.println("Сохранен турнир Гранд Финал");

        /// 4) Тест метода addParticipant
        System.out.println("Тест метода addParticipant() - регистрация Alice на турнир");
        tournamentRepository.addParticipant(savedTour.getId(), savedAlice.getId());

        /// 5) Тест метода findUsersByTournamentId
        System.out.println("Тест метода findUsersByTournamentId()");
        List<User> participants = userRepository.findUsersByTournamentId(savedTour.getId());

        Assertions.assertNotNull(participants, "Список участников не должен быть null");
        Assertions.assertFalse(participants.isEmpty(), "Список участников не должен быть пустым");
        Assertions.assertEquals(1, participants.size(), "В списке должен быть ровно 1 участник");
        Assertions.assertEquals("Alice", participants.get(0).getUsername(), "Имя участника должно совпадать с зарегистрированным");
        System.out.println("Связь успешно отработала, Alice найдена в списке участников турнира");

        System.out.println("Очистка тестовых данных после проверки связей");
        tournamentRepository.deleteById(savedTour.getId());
        userRepository.deleteByUsername("Alice");
        System.out.println("Тестовые данные удалены");
    }
}
