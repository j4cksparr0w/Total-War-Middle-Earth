package com.marija.middleearthbattle.config;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

public record UnitConfiguration(
        String name,
        Faction faction,
        UnitType unitType,
        int maxHealth,
        int attack,
        int defense
) {
}