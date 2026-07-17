package com.baev.tournament.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tournament {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
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
        this.status = TournamentStatus.REGISTRATION;
    }
}
