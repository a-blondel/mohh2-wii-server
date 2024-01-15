package com.ea.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PERSONA_STATS")
public class PersonaStatsEntity {

    @Id
    @Column(name = "PERSONA_ID")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name="PERSONA_ID", nullable=false)
    private PersonaEntity persona;
    private int totalKills;
    private int totalDeaths;
    private int totalHeadshots;
    private int totalShots;
    private int totalMissedShots;
    private int timeAsAllied;
    private int timeAsAxis;
    private int timeInPort;
    private int timeInCity;
    private int timeInSewers;
    private int timeInVillage;
    private int timeInMonastery;
    private int timeInBase;
    private int timeInDm;
    private int timeInTdm;
    private int timeInInf;
    private int dmWins;
    private int dmLosses;
    private int tdmWins;
    private int tdmLosses;
    private int infWins;
    private int infLosses;
    private int coltKills;
    private int coltShots;
    private int coltMissedShots;
    private int thompsonKills;
    private int thompsonShots;
    private int thompsonMissedShots;
    private int barKills;
    private int barShots;
    private int barMissedShots;
    private int garandKills;
    private int garandShots;
    private int garandMissedShots;
    private int springfieldKills;
    private int springfieldShots;
    private int springfieldMissedShots;
    private int shotgunKills;
    private int shotgunShots;
    private int shotgunMissedShots;
    private int bazookaKills;
    private int bazookaShots;
    private int bazookaMissedShots;
    private int lugerKills;
    private int lugerShots;
    private int lugerMissedShots;
    private int mpKills;
    private int mpShots;
    private int mpMissedShots;
    private int stgKills;
    private int stgShots;
    private int stgMissedShots;
    private int karabinerKills;
    private int karabinerShots;
    private int karabinerMissedShots;
    private int gewehrKills;
    private int gewehrShots;
    private int gewehrMissedShots;
    private int grenadeKills;
    private int meleeKills;

}
