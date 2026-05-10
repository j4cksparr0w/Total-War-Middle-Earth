package com.marija.middleearthbattle.save;

import com.marija.middleearthbattle.model.Faction;

import java.io.Serializable;
import java.util.List;

public record SavedPlayer(
        String name,
        Faction faction,
        List<SavedUnit> units
) implements Serializable {
}