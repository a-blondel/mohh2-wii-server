package com.ea.enums;

public enum MapEnum {
    PORT("0"),
    CITY("2"),
    SEWERS("4"),
    VILLAGE("7"),
    MONASTERY("10"),
    BASE("12");

    public final String value;

    MapEnum(String value) {
        this.value = value;
    }
}
