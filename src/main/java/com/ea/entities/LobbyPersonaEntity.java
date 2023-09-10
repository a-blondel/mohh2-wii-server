package com.ea.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "LOBBY_PERSONA")
public class LobbyPersonaEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="LOBBY_ID", nullable=false)
    private LobbyEntity lobby;

    @ManyToOne
    @JoinColumn(name="PERSONA_ID", nullable=false)
    private PersonaEntity persona;

    private int kills;

    private int deaths;

    private boolean inLobby;

}
