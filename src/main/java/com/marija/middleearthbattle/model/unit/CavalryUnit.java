package com.marija.middleearthbattle.model.unit;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

public class CavalryUnit extends GameUnit {

    public CavalryUnit(String name, Faction faction) {
        this(name, faction, 90, 23, 7);
    }

    public CavalryUnit(String name, Faction faction, int maxHealth, int attack, int defense) {
        super(name, faction, UnitType.CAVALRY, maxHealth, attack, defense);
    }
}