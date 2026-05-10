package com.marija.middleearthbattle.model.unit;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

public class RangedUnit extends GameUnit {

    public RangedUnit(String name, Faction faction) {
        this(name, faction, 65, 21, 5);
    }

    public RangedUnit(String name, Faction faction, int maxHealth, int attack, int defense) {
        super(name, faction, UnitType.RANGED, maxHealth, attack, defense);
    }
}