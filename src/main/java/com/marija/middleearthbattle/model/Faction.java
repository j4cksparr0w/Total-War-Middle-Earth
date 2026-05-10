package com.marija.middleearthbattle.model;

public enum Faction {
    GONDOR("Gondor"),
    MORDOR("Mordor");

    private final String displayName;

    Faction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}