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
    private int totalHit;
    private int totalMiss;
    private int timeAllied;
    private int timeAxis;
    private int timePort;
    private int timeCity;
    private int timeSewers;
    private int timeVillage;
    private int timeMonastery;
    private int timeBase;
    private int timeDm;
    private int timeTdm;
    private int timeInf;
    private int dmWins;
    private int dmLosses;
    private int tdmWins;
    private int tdmLosses;
    private int infWins;
    private int infLosses;
    private int coltKills;
    private int coltHit;
    private int coltMiss;
    private int thompsonKills;
    private int thompsonHit;
    private int thompsonMiss;
    private int barKills;
    private int barHit;
    private int barMiss;
    private int garandKills;
    private int garandHit;
    private int garandMiss;
    private int springfieldKills;
    private int springfieldHit;
    private int springfieldMiss;
    private int shotgunKills;
    private int shotgunHit;
    private int shotgunMiss;
    private int bazookaKills;
    private int bazookaHit;
    private int bazookaMiss;
    private int lugerKills;
    private int lugerHit;
    private int lugerMiss;
    @Column(name = "MP40_KILLS")
    private int mp40Kills;
    @Column(name = "MP40_HIT")
    private int mp40Hit;
    @Column(name = "MP40_MISS")
    private int mp40Miss;
    @Column(name = "STG44_KILLS")
    private int stg44Kills;
    @Column(name = "STG44_HIT")
    private int stg44Hit;
    @Column(name = "STG44_MISS")
    private int stg44Miss;
    private int karabinerKills;
    private int karabinerHit;
    private int karabinerMiss;
    private int gewehrKills;
    private int gewehrHit;
    private int gewehrMiss;
    private int grenadeKills;
    private int meleeKills;

}
