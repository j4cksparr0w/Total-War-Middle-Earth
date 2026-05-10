package com.marija.middleearthbattle.model;

public class GameState {

    private final Player gondorPlayer;
    private final Player mordorPlayer;
    private Faction currentTurn;
    private GameStatus status;

    public GameState(Player gondorPlayer, Player mordorPlayer) {
        this.gondorPlayer = gondorPlayer;
        this.mordorPlayer = mordorPlayer;
        this.currentTurn = Faction.GONDOR;
        this.status = GameStatus.READY;
    }

    public void switchTurn() {
        currentTurn = currentTurn == Faction.GONDOR ? Faction.MORDOR : Faction.GONDOR;
    }

    public Player getCurrentPlayer() {
        return currentTurn == Faction.GONDOR ? gondorPlayer : mordorPlayer;
    }

    public Player getOpponentPlayer() {
        return currentTurn == Faction.GONDOR ? mordorPlayer : gondorPlayer;
    }

    public Player getGondorPlayer() {
        return gondorPlayer;
    }

    public Player getMordorPlayer() {
        return mordorPlayer;
    }

    public Faction getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Faction currentTurn) {
        this.currentTurn = currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}