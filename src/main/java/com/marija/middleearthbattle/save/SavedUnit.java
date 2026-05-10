package com.marija.middleearthbattle.save;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

import java.io.Serializable;

public record SavedUnit(
        String name,
        Faction faction,
        UnitType unitType,
        int health,
        int maxHealth,
        int attack,
        int defense
) implements Serializable {
}