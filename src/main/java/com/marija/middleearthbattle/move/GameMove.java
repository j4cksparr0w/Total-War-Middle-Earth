package com.marija.middleearthbattle.move;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record GameMove(
        UUID id,
        Faction attackerFaction,
        UnitType attackerUnitType,
        UnitType defenderUnitType,
        LocalDateTime createdAt
) implements Serializable {

    public static GameMove from(Faction attackerFaction, UnitType attackerUnitType, UnitType defenderUnitType) {
        return new GameMove(
                UUID.randomUUID(),
                attackerFaction,
                attackerUnitType,
                defenderUnitType,
                LocalDateTime.now()
        );
    }
}