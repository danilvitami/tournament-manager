package com.baev.tournament.repository;

import com.baev.tournament.model.User;
import java.util.List;

public interface UserRepository {
    User save(User user);

    User findByUsername(String username);

    void deleteByUsername(String username);
    List<User> findUsersByTournamentId(Long tournamentId);
}
