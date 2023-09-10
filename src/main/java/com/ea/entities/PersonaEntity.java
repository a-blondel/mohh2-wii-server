package com.ea.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PERSONA")
public class PersonaEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID", nullable=false)
    private AccountEntity account;

    private String pers;

    private long kills;

    private long deaths;

    private int rp;

    private Timestamp createdOn;

    private Timestamp deletedOn;

    @OneToMany(mappedBy="persona", fetch = FetchType.LAZY)
    private Set<LobbyPersonaEntity> lobbyPersonas;

}
