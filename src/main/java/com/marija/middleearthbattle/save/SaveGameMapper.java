package com.marija.middleearthbattle.save;

import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.model.Player;
import com.marija.middleearthbattle.model.unit.CavalryUnit;
import com.marija.middleearthbattle.model.unit.CommanderUnit;
import com.marija.middleearthbattle.model.unit.GameUnit;
import com.marija.middleearthbattle.model.unit.InfantryUnit;
import com.marija.middleearthbattle.model.unit.MonsterUnit;
import com.marija.middleearthbattle.model.unit.RangedUnit;

import java.time.LocalDateTime;
import java.util.List;

public final class SaveGameMapper {

    private SaveGameMapper() {
    }

    public static SavedGameState toSavedGameState(GameState gameState) {
        return new SavedGameState(
                toSavedPlayer(gameState.getGondorPlayer()),
                toSavedPlayer(gameState.getMordorPlayer()),
                gameState.getCurrentTurn(),
                gameState.getStatus(),
                LocalDateTime.now()
        );
    }

    public static GameState toGameState(SavedGameState savedGameState) {
        Player gondorPlayer = toPlayer(savedGameState.gondorPlayer());
        Player mordorPlayer = toPlayer(savedGameState.mordorPlayer());

        GameState gameState = new GameState(gondorPlayer, mordorPlayer);
        gameState.setCurrentTurn(savedGameState.currentTurn());
        gameState.setStatus(savedGameState.status());

        return gameState;
    }

    private static SavedPlayer toSavedPlayer(Player player) {
        List<SavedUnit> units = player.getUnits()
                .stream()
                .map(SaveGameMapper::toSavedUnit)
                .toList();

        return new SavedPlayer(player.getName(), player.getFaction(), units);
    }

    private static SavedUnit toSavedUnit(GameUnit unit) {
        return new SavedUnit(
                unit.getName(),
                unit.getFaction(),
                unit.getUnitType(),
                unit.getHealth(),
                unit.getMaxHealth(),
                unit.getAttack(),
                unit.getDefense()
        );
    }

    private static Player toPlayer(SavedPlayer savedPlayer) {
        Player player = new Player(savedPlayer.name(), savedPlayer.faction());

        savedPlayer.units()
                .stream()
                .map(SaveGameMapper::toUnit)
                .forEach(unit -> player.getUnits().add(unit));

        return player;
    }

    private static GameUnit toUnit(SavedUnit savedUnit) {
        GameUnit unit = switch (savedUnit.unitType()) {
            case COMMANDER -> new CommanderUnit(
                    savedUnit.name(),
                    savedUnit.faction(),
                    savedUnit.maxHealth(),
                    savedUnit.attack(),
                    savedUnit.defense()
            );
            case INFANTRY -> new InfantryUnit(
                    savedUnit.name(),
                    savedUnit.faction(),
                    savedUnit.maxHealth(),
                    savedUnit.attack(),
                    savedUnit.defense()
            );
            case RANGED -> new RangedUnit(
                    savedUnit.name(),
                    savedUnit.faction(),
                    savedUnit.maxHealth(),
                    savedUnit.attack(),
                    savedUnit.defense()
            );
            case CAVALRY -> new CavalryUnit(
                    savedUnit.name(),
                    savedUnit.faction(),
                    savedUnit.maxHealth(),
                    savedUnit.attack(),
                    savedUnit.defense()
            );
            case MONSTER -> new MonsterUnit(
                    savedUnit.name(),
                    savedUnit.faction(),
                    savedUnit.maxHealth(),
                    savedUnit.attack(),
                    savedUnit.defense()
            );
        };

        unit.restoreHealth(savedUnit.health());
        return unit;
    }
}
