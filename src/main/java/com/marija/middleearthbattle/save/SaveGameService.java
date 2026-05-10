package com.marija.middleearthbattle.save;

import com.marija.middleearthbattle.model.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveGameService {

    private static final Path SAVE_DIRECTORY = Path.of("saves");
    private static final Path SAVE_FILE = SAVE_DIRECTORY.resolve("middle-earth-battle-save.dat");

    public void save(GameState gameState) throws IOException {
        Files.createDirectories(SAVE_DIRECTORY);

        SavedGameState savedGameState = SaveGameMapper.toSavedGameState(gameState);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(SAVE_FILE))) {
            objectOutputStream.writeObject(savedGameState);
        }
    }

    public GameState load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(SAVE_FILE))) {
            SavedGameState savedGameState = (SavedGameState) objectInputStream.readObject();
            return SaveGameMapper.toGameState(savedGameState);
        }
    }

    public boolean saveFileExists() {
        return Files.exists(SAVE_FILE);
    }
}