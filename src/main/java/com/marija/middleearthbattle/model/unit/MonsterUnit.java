package com.marija.middleearthbattle.model.unit;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

public class MonsterUnit extends GameUnit {

    public MonsterUnit(String name, Faction faction) {
        this(name, faction, 130, 30, 4);
    }

    public MonsterUnit(String name, Faction faction, int maxHealth, int attack, int defense) {
        super(name, faction, UnitType.MONSTER, maxHealth, attack, defense);
    }
}