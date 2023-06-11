package com.ea.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity {

    @Id
    private long id;

    private String mail;

    private String pass;

    private String loc;

    private String name;

    private Timestamp createdOn;

    @OneToMany(mappedBy="account")
    private Set<PlayerEntity> players;

}
