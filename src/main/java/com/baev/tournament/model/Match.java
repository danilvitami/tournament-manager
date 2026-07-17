package com.baev.tournament.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Match {
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