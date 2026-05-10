package com.marija.middleearthbattle.engine;

import com.marija.middleearthbattle.model.GameStatus;
import com.marija.middleearthbattle.model.unit.GameUnit;

public record AttackResult(
        GameUnit attacker,
        GameUnit defender,
        int damage,
        boolean defenderDefeated,
        GameStatus gameStatus
) {

    public String message() {
        String result = attacker.getName() + " attacked " + defender.getName() + " for " + damage + " damage.";

        if (defenderDefeated) {
            result += " " + defender.getName() + " has been defeated.";
        }

        if (gameStatus == GameStatus.GONDOR_WON) {
            result += " Gondor has won the battle.";
        }

        if (gameStatus == GameStatus.MORDOR_WON) {
            result += " Mordor has won the battle.";
        }

        return result;
    }
}