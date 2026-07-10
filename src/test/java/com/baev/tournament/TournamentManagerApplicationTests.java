package com.baev.tournament;

import com.baev.tournament.repository.UserRepository;
import com.baev.tournament.repository.TournamentRepository;
import com.baev.tournament.model.User;
import com.baev.tournament.model.Tournament;
import com.baev.tournament.model.Role;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void TestOperations(){
        /// 1)deleteByUsername
        System.out.println("Очистка данных пользователя");
        userRepository.deleteByUsername("Tom");
        System.out.println("База очищена");


        System.out.println("Очистка данных пользователя");
        userRepository.deleteByUsername("Bob");
        System.out.println("База очищена");

        /// 2) Save для User
        System.out.println("Тест метода save() для пользователя");
        User Tom = new User("Tom","Secret07", "Tom07@xmail.ru", Role.PLAYER);
        userRepository.save(Tom);
        System.out.println("сохранил пользователя");
//
        User Bob = new User();
        Bob.setUsername("Bob");
        Bob.setPassword("aBc2233");
        Bob.setEmail("Bob9@gmail.com");
        Bob.setRole(Role.PLAYER);
        userRepository.save(Bob);
        System.out.println("Сохранил пользвателя(2)");

        /// 3) findByUsername
        System.out.println("Тест метода findByUsername");
        User foundPers = userRepository.findByUsername("Tom");
        if (foundPers != null){
            System.out.printf("Пользователь найден::%d\nЛогин:%S\nEmail:%S\nРоль:%s\n",foundPers.getId(),foundPers.getUsername(),
                    foundPers.getEmail(),foundPers.getRole());
        }
        else{
            System.out.println("Ошибка");
        }

        /// 4) save для Tournament
        System.out.println("Тест метода save() для турнира");
        Tournament tour_1 = new Tournament("Кубок Саратова", "Для любителей", "Шахматы",10, 32);
        tournamentRepository.save(tour_1);
        System.out.println("Турнир добавлен");
    }
}
