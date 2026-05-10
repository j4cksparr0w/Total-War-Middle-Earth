package com.marija.middleearthbattle.model;

public enum UnitType {
    COMMANDER("Commander"),
    INFANTRY("Infantry"),
    RANGED("Ranged"),
    CAVALRY("Cavalry"),
    MONSTER("Monster");

    private final String displayName;

    UnitType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}