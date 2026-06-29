package com.baev.tournament.repository;

import com.baev.tournament.model.User;


public interface UserRepository {
    User save(User user);
//Должен будет возвращать User, если нашел, и null, если такого логина нет
    User findByUsername(String username);
}
