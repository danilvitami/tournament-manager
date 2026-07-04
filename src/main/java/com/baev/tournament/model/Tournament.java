package com.baev.tournament.model;

import lombok.Data;

@Data
public class    Tournament {
    private Long id;
    private String name;
    private String description;//Описание
    private String discipline;
    //участники
    private int minParticipants;
    private int maxParticipants;

    private TournamentStatus status;

    public Tournament(String name, String description, String discipline, int minParticipants, int maxParticipants) {
        this.name = name;
        this.description = description;
        this.discipline = discipline;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.status = TournamentStatus.REGISTRATION;
    }

    public Tournament() {
        this.status = TournamentStatus.REGISTRATION;//каждому новому обьекту мы присваиваем статут регистрации
    }
}
