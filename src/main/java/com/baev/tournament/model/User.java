package com.baev.tournament.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.Objects;
@Data//геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Role role;


    public User(String username, String password, String email, Role role){
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        User user = (User)o;
//        return id != 0 && this.id.equals(user.id);
//    }
}
