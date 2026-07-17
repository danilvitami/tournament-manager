package com.baev.tournament.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Long tournamentId;
    private Long player1Id;
    private Long player2Id;
    private Integer score1;
    private Integer score2;
    private Long winnerId;
    private String stage;
    private Long nextMatchId;
}