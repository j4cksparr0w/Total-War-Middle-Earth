package com.marija.middleearthbattle.engine;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.model.Player;
import com.marija.middleearthbattle.model.UnitType;
import com.marija.middleearthbattle.model.unit.GameUnit;
import com.marija.middleearthbattle.move.GameMove;

public class BattleMoveService {

    private final GameEngine gameEngine = new GameEngine();

    public AttackResult executeSelectedMove(GameState gameState, GameUnit attacker, GameUnit defender) {
        return gameEngine.attack(gameState, attacker, defender);
    }

    public AttackResult executeNetworkMove(GameState gameState, GameMove gameMove) {
        Player attackerPlayer = findPlayer(gameState, gameMove.attackerFaction());
        Player defenderPlayer = findPlayer(gameState, findOpponentFaction(gameMove.attackerFaction()));

        GameUnit attacker = findUnit(attackerPlayer, gameMove.attackerUnitType());
        GameUnit defender = findUnit(defenderPlayer, gameMove.defenderUnitType());

        return gameEngine.attack(gameState, attacker, defender);
    }

    private Player findPlayer(GameState gameState, Faction faction) {
        if (faction == Faction.GONDOR) {
            return gameState.getGondorPlayer();
        }

        return gameState.getMordorPlayer();
    }

    private Faction findOpponentFaction(Faction faction) {
        return faction == Faction.GONDOR ? Faction.MORDOR : Faction.GONDOR;
    }

    private GameUnit findUnit(Player player, UnitType unitType) {
        return player.getUnits()
                .stream()
                .filter(unit -> unit.getUnitType() == unitType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Network move contains an unknown unit."));
    }
}
