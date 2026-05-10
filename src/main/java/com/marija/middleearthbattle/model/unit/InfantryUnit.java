package com.marija.middleearthbattle.model.unit;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

public class InfantryUnit extends GameUnit {

    public InfantryUnit(String name, Faction faction) {
        this(name, faction, 80, 18, 10);
    }

    public InfantryUnit(String name, Faction faction, int maxHealth, int attack, int defense) {
        super(name, faction, UnitType.INFANTRY, maxHealth, attack, defense);
    }
}