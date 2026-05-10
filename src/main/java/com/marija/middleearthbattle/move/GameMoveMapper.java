package com.marija.middleearthbattle.move;

import com.marija.middleearthbattle.model.unit.GameUnit;

public final class GameMoveMapper {

    private GameMoveMapper() {
    }

    public static GameMove fromUnits(GameUnit attacker, GameUnit defender) {
        return GameMove.from(
                attacker.getFaction(),
                attacker.getUnitType(),
                defender.getUnitType()
        );
    }
}