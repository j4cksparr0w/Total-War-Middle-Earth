package com.marija.middleearthbattle.model.unit;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

public class CommanderUnit extends GameUnit {

    public CommanderUnit(String name, Faction faction) {
        this(name, faction, 100, 24, 8);
    }

    public CommanderUnit(String name, Faction faction, int maxHealth, int attack, int defense) {
        super(name, faction, UnitType.COMMANDER, maxHealth, attack, defense);
    }
}