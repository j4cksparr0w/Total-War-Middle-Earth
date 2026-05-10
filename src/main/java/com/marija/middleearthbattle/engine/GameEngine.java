package com.marija.middleearthbattle.engine;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.model.GameStatus;
import com.marija.middleearthbattle.model.UnitType;
import com.marija.middleearthbattle.model.unit.GameUnit;

import java.util.concurrent.ThreadLocalRandom;

public class GameEngine {

    public AttackResult attack(GameState gameState, GameUnit attacker, GameUnit defender) {
        validateAttack(gameState, attacker, defender);

        if (gameState.getStatus() == GameStatus.READY) {
            gameState.setStatus(GameStatus.IN_PROGRESS);
        }

        int damage = calculateDamage(attacker, defender);
        defender.receiveDamage(damage);

        boolean defenderDefeated = !defender.isAlive();
        GameStatus newStatus = determineStatus(gameState);
        gameState.setStatus(newStatus);

        if (newStatus == GameStatus.IN_PROGRESS) {
            gameState.switchTurn();
        }

        return new AttackResult(attacker, defender, damage, defenderDefeated, newStatus);
    }

    private void validateAttack(GameState gameState, GameUnit attacker, GameUnit defender) {
        if (attacker == null) {
            throw new IllegalArgumentException("Please choose an attacking unit.");
        }

        if (defender == null) {
            throw new IllegalArgumentException("Please choose a target unit.");
        }

        if (!attacker.isAlive()) {
            throw new IllegalArgumentException("Selected attacking unit has been defeated.");
        }

        if (!defender.isAlive()) {
            throw new IllegalArgumentException("Selected target unit has already been defeated.");
        }

        if (attacker.getFaction() != gameState.getCurrentTurn()) {
            throw new IllegalArgumentException("It is not this unit's turn.");
        }

        if (attacker.getFaction() == defender.getFaction()) {
            throw new IllegalArgumentException("You cannot attack your own army.");
        }

        if (gameState.getStatus() == GameStatus.GONDOR_WON || gameState.getStatus() == GameStatus.MORDOR_WON) {
            throw new IllegalArgumentException("The battle is already finished.");
        }
    }

    private int calculateDamage(GameUnit attacker, GameUnit defender) {
        int baseDamage = attacker.getAttack() - defender.getDefense();
        int randomBonus = ThreadLocalRandom.current().nextInt(1, 7);
        int typeBonus = calculateTypeBonus(attacker.getUnitType(), defender.getUnitType());

        return Math.max(1, baseDamage + randomBonus + typeBonus);
    }

    private int calculateTypeBonus(UnitType attackerType, UnitType defenderType) {
        if (attackerType == UnitType.CAVALRY && defenderType == UnitType.RANGED) {
            return 6;
        }

        if (attackerType == UnitType.RANGED && defenderType == UnitType.INFANTRY) {
            return 5;
        }

        if (attackerType == UnitType.INFANTRY && defenderType == UnitType.CAVALRY) {
            return 4;
        }

        if (attackerType == UnitType.MONSTER && defenderType == UnitType.COMMANDER) {
            return 4;
        }

        if (attackerType == UnitType.COMMANDER && defenderType == UnitType.MONSTER) {
            return 3;
        }

        return 0;
    }

    private GameStatus determineStatus(GameState gameState) {
        boolean gondorAlive = gameState.getGondorPlayer().hasLivingUnits();
        boolean mordorAlive = gameState.getMordorPlayer().hasLivingUnits();

        if (!gondorAlive) {
            return GameStatus.MORDOR_WON;
        }

        if (!mordorAlive) {
            return GameStatus.GONDOR_WON;
        }

        return GameStatus.IN_PROGRESS;
    }
}