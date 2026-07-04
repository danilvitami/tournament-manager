package com.baev.tournament.model;

public class Tournament {
    private Long id;
    private String name;
    private String description;//Описание
    private String discipline;
    //участники
    private int minParticipants;
    private int maxParticipants;

    private TournamentStatus status;

    public Tournament(String name, String description, String discipline, int minParticipants,int maxParticipants){
        this.name = name;
        this.description = description;
        this.discipline = discipline;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.status = TournamentStatus.REGISTRATION;
    }
    public Tournament(){
        this.status = TournamentStatus.REGISTRATION;//каждому новому обьекту мы присваиваем статут регистрации
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getName(){return name;}
    public void SetName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getDiscipline(){return discipline;}
    public void setDiscipline(String discipline){this.discipline = discipline;}

    public int getMinParticipants(){return minParticipants;}
    public void setMinParticipants(int minParticipants){this.minParticipants = minParticipants;}

    public int getMaxParticipants(){return maxParticipants;}
    public void getMaxParticipants(int maxParticipants) {this.maxParticipants = maxParticipants;}

    public TournamentStatus getStatus(){return status;}
    public void setStatus(TournamentStatus status){this.status = status;}
    }

