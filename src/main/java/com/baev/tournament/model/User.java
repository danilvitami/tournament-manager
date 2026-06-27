package com.baev.tournament.model;

import java.util.Objects;

public class User {
    private Long id;
    private String Username;
    private String password;
    private String email;
    private Role role;

    public User(String Username, String password, String email, Role role){
        this.Username = Username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
    public Long getId(){return id;}
    public void setId(Long id){ this.id = id;}

    public String getUsername(){ return Username;}
    public void setUsername(String username){ this.Username = Username;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}

    public String getEmail(){return email;}
    public void setEmail(String password){this.email = email;}

    public Role getRole(){return role;}
    public void setRole(Role role){this.role = role;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User)o;
        return id != 0 && this.id.equals(user.id);
    }
}


