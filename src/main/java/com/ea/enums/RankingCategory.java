package com.ea.enums;

public enum RankingCategory {
    MY_EA_LEADERBOARD("0"),
    EA_TOP_100("1"),
    EA_WEAPON_LEADERS("2");

    public final String value;

    RankingCategory(String value) {
        this.value = value;
    }
}
