package com.baev.tournament;

import org.springframework.boot.SpringApplication;//метод run() для старта всего Spring-приложения
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baev.tournament.model.Role;
import com.baev.tournament.model.User;
import com.baev.tournament.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;//для автоматического выполнения фрагмента кода сразу после запуска
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TournamentManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournamentManagerApplication.class, args);}

    @Bean
    public CommandLineRunner testApp(UserRepository userRepository){
        return args ->{
            /// 0)
            System.out.println("Очистка старых данных пользователя");
            userRepository.deleteByUsername("Tom");
            System.out.println("База очищена");
            /// 1)
//            System.out.println("Тест Save()");
//            User Tom = new User();
//            Tom.setUsername("Tom");
//            Tom.setPassword("Secret07");
//            Tom.setEmail("Tom07@xmail.ru");
//            Tom.setRole(Role.PLAYER);
//
//            userRepository.save(Tom);
//            System.out.println("save() работает");

            /// 2)
            System.out.println("Тест findByUsername()");

            User foundTom = userRepository.findByUsername("Tom");

            if (foundTom != null){
                System.out.printf("Пользователь найден.\nID из базы:%d\nЛогин:%S\nEmail:%S\nРоль:%s\n",foundTom.getId(),foundTom.getUsername(),
                        foundTom.getEmail(),foundTom.getRole());}
            else {System.out.println("Ошибка");}

            /// 3)
            System.out.println("Тест findByUsername() для несуществующего пользователя");
            User foundBilbo = userRepository.findByUsername("Bilbo");

            if(foundBilbo == null){
                System.out.println("Пользователь не найден(успех)");
            }
            else{System.out.println("Ошибка,кого то нашел");
        };
    };}
    }
