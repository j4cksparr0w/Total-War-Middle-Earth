package com.marija.middleearthbattle;

import com.marija.middleearthbattle.audio.MusicService;
import com.marija.middleearthbattle.documentation.DocumentationService;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.save.SaveGameService;
import com.marija.middleearthbattle.ui.ImageService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainMenuController {

    private final DocumentationService documentationService = new DocumentationService();
    private final SaveGameService saveGameService = new SaveGameService();

    @FXML
    private BorderPane mainMenuRoot;

    @FXML
    private Label statusLabel;

    @FXML
    private Button musicToggleButton;

    @FXML
    private void initialize() {
        configureBackgroundImage();
        updateMusicToggleButton();
    }

    @FXML
    private void handleNewGame() {
        try {
            MiddleEarthBattleApplication.showBattleScreen();
        } catch (IOException exception) {
            statusLabel.setText("Battle screen could not be loaded.");
        }
    }

    @FXML
    private void handleLoadGame() {
        try {
            if (!saveGameService.saveFileExists()) {
                statusLabel.setText("There is no saved game yet.");
                return;
            }

            GameState loadedGameState = saveGameService.load();
            MiddleEarthBattleApplication.showLoadedGameScreen(loadedGameState);
        } catch (IOException | ClassNotFoundException exception) {
            statusLabel.setText("Saved game could not be loaded.");
        }
    }

    @FXML
    private void handleHostGame() {
        try {
            MiddleEarthBattleApplication.openGondorPlayerWindow();
            statusLabel.setText("Gondor player window opened.");
        } catch (IOException exception) {
            statusLabel.setText("Gondor player window could not be opened.");
        }
    }

    @FXML
    private void handleJoinGame() {
        try {
            MiddleEarthBattleApplication.openMordorPlayerWindow();
            statusLabel.setText("Mordor player window opened.");
        } catch (IOException exception) {
            statusLabel.setText("Mordor player window could not be opened.");
        }
    }

    @FXML
    private void handleGenerateDocumentation() {
        try {
            statusLabel.setText("Documentation generated at: " + documentationService.generateDocumentation().toAbsolutePath());
        } catch (IOException exception) {
            statusLabel.setText("Documentation could not be generated.");
        }
    }

    @FXML
    private void handleToggleMusic() {
        MusicService.toggleMute();
        updateMusicToggleButton();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void configureBackgroundImage() {
        ImageService.setBackgroundImageIfExists(mainMenuRoot, "backgrounds/menu_background.jpg");
    }

    private void updateMusicToggleButton() {
        musicToggleButton.setText(MusicService.isMuted() ? "Music Off" : "Music On");
    }
}