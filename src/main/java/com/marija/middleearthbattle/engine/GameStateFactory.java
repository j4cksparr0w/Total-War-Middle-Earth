package com.marija.middleearthbattle.engine;

import com.marija.middleearthbattle.config.GameConfiguration;
import com.marija.middleearthbattle.config.GameConfigurationService;
import com.marija.middleearthbattle.config.UnitConfiguration;
import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.model.Player;
import com.marija.middleearthbattle.model.unit.CavalryUnit;
import com.marija.middleearthbattle.model.unit.CommanderUnit;
import com.marija.middleearthbattle.model.unit.GameUnit;
import com.marija.middleearthbattle.model.unit.InfantryUnit;
import com.marija.middleearthbattle.model.unit.MonsterUnit;
import com.marija.middleearthbattle.model.unit.RangedUnit;

public final class GameStateFactory {

    private static final GameConfigurationService gameConfigurationService = new GameConfigurationService();

    private GameStateFactory() {
    }

    public static GameState createDefaultGameState() {
        GameConfiguration gameConfiguration = gameConfigurationService.loadOrCreateDefaultConfiguration();

        Player gondorPlayer = new Player("Player 1", Faction.GONDOR);
        Player mordorPlayer = new Player("Player 2", Faction.MORDOR);

        for (UnitConfiguration unitConfiguration : gameConfiguration.units()) {
            GameUnit unit = createUnit(unitConfiguration);

            if (unit.getFaction() == Faction.GONDOR) {
                gondorPlayer.getUnits().add(unit);
            }

            if (unit.getFaction() == Faction.MORDOR) {
                mordorPlayer.getUnits().add(unit);
            }
        }

        return new GameState(gondorPlayer, mordorPlayer);
    }

    private static GameUnit createUnit(UnitConfiguration unitConfiguration) {
        return switch (unitConfiguration.unitType()) {
            case COMMANDER -> new CommanderUnit(
                    unitConfiguration.name(),
                    unitConfiguration.faction(),
                    unitConfiguration.maxHealth(),
                    unitConfiguration.attack(),
                    unitConfiguration.defense()
            );
            case INFANTRY -> new InfantryUnit(
                    unitConfiguration.name(),
                    unitConfiguration.faction(),
                    unitConfiguration.maxHealth(),
                    unitConfiguration.attack(),
                    unitConfiguration.defense()
            );
            case RANGED -> new RangedUnit(
                    unitConfiguration.name(),
                    unitConfiguration.faction(),
                    unitConfiguration.maxHealth(),
                    unitConfiguration.attack(),
                    unitConfiguration.defense()
            );
            case CAVALRY -> new CavalryUnit(
                    unitConfiguration.name(),
                    unitConfiguration.faction(),
                    unitConfiguration.maxHealth(),
                    unitConfiguration.attack(),
                    unitConfiguration.defense()
            );
            case MONSTER -> new MonsterUnit(
                    unitConfiguration.name(),
                    unitConfiguration.faction(),
                    unitConfiguration.maxHealth(),
                    unitConfiguration.attack(),
                    unitConfiguration.defense()
            );
        };
    }
}