package com.marija.middleearthbattle.async;

import com.marija.middleearthbattle.engine.GameStateFactory;
import com.marija.middleearthbattle.model.GameState;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GameLoadingService extends Service<GameState> {

    @Override
    protected Task<GameState> createTask() {
        return new Task<>() {
            @Override
            protected GameState call() {
                updateMessage("Loading game configuration...");
                GameState gameState = GameStateFactory.createDefaultGameState();
                updateMessage("Game configuration loaded.");
                return gameState;
            }
        };
    }
}