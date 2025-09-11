package com.example.ex2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "authorities")
public class Authority {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
