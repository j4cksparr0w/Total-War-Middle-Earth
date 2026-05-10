package com.marija.middleearthbattle;

import com.marija.middleearthbattle.audio.MusicService;
import com.marija.middleearthbattle.engine.GameStateFactory;
import com.marija.middleearthbattle.model.BattleMode;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.ui.FontService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class MiddleEarthBattleApplication extends Application {

    private static Stage primaryStage;
    private static BattleMode selectedBattleMode = BattleMode.LOCAL_TEST;
    private static GameState sharedGameState;
    private static boolean sharedBattleSession;
    private static GameState pendingInitialGameState;
    private static final List<BattleController> activeBattleControllers = new CopyOnWriteArrayList<>();
    private static final Set<BattleMode> openBattleModes = EnumSet.noneOf(BattleMode.class);
    private static final List<String> sharedBattleLog = new CopyOnWriteArrayList<>();
    private static Runnable shutdownAction = () -> {
    };

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setOnCloseRequest(event -> executeShutdownAction());
        showMainMenu();
    }

    public static void showMainMenu() throws IOException {
        clearShutdownAction();
        showScene("main-menu-view.fxml", "Middle-earth Battle", 1280, 820);
        MusicService.playMenuTheme();
    }

    public static void showBattleScreen() throws IOException {
        sharedBattleSession = false;
        sharedGameState = null;
        pendingInitialGameState = null;
        activeBattleControllers.clear();
        openBattleModes.clear();
        sharedBattleLog.clear();
        selectedBattleMode = BattleMode.LOCAL_TEST;
        showScene("battle-view.fxml", "Battle of Middle-earth", 1500, 920);
        MusicService.playBattleTheme();
    }

    public static void showLoadedGameScreen(GameState gameState) throws IOException {
        sharedBattleSession = false;
        sharedGameState = null;
        pendingInitialGameState = gameState;
        activeBattleControllers.clear();
        openBattleModes.clear();
        sharedBattleLog.clear();
        selectedBattleMode = BattleMode.LOCAL_TEST;
        showScene("battle-view.fxml", "Loaded Battle of Middle-earth", 1500, 920);
        MusicService.playBattleTheme();
    }

    public static GameState consumePendingInitialGameState() {
        GameState gameState = pendingInitialGameState;
        pendingInitialGameState = null;
        return gameState;
    }

    public static void openGondorPlayerWindow() throws IOException {
        openSharedBattleWindow(BattleMode.GONDOR_PLAYER, "Gondor Player - Battle of Middle-earth");
    }

    public static void openMordorPlayerWindow() throws IOException {
        openSharedBattleWindow(BattleMode.MORDOR_PLAYER, "Mordor Player - Battle of Middle-earth");
    }

    public static BattleMode getSelectedBattleMode() {
        return selectedBattleMode;
    }

    public static boolean isSharedBattleSession() {
        return sharedBattleSession;
    }

    public static GameState getSharedGameState() {
        ensureSharedGameState();
        return sharedGameState;
    }

    public static void replaceSharedGameState(GameState gameState) {
        sharedGameState = gameState;
    }

    public static void registerBattleController(BattleController controller) {
        activeBattleControllers.add(controller);
    }

    public static void unregisterBattleController(BattleController controller) {
        activeBattleControllers.remove(controller);

        if (activeBattleControllers.isEmpty()) {
            sharedBattleSession = false;
            sharedGameState = null;
            openBattleModes.clear();
            sharedBattleLog.clear();
            MusicService.playMenuTheme();
        }
    }

    public static void refreshSharedBattleWindows() {
        activeBattleControllers.forEach(BattleController::refreshSharedBattleWindow);
    }

    public static void appendSharedBattleLog(String message) {
        sharedBattleLog.add(message);
    }

    public static String getSharedBattleLogText() {
        return String.join(System.lineSeparator(), sharedBattleLog);
    }

    public static void setShutdownAction(Runnable action) {
        shutdownAction = action != null ? action : () -> {
        };
    }

    public static void clearShutdownAction() {
        shutdownAction = () -> {
        };
    }

    private static void openSharedBattleWindow(BattleMode battleMode, String title) throws IOException {
        if (openBattleModes.contains(battleMode)) {
            return;
        }

        sharedBattleSession = true;
        ensureSharedGameState();

        if (sharedBattleLog.isEmpty()) {
            sharedBattleLog.add("Shared two-player battle session started.");
        }

        selectedBattleMode = battleMode;
        openBattleModes.add(battleMode);

        FXMLLoader fxmlLoader = new FXMLLoader(MiddleEarthBattleApplication.class.getResource("battle-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 920);
        scene.getStylesheets().add(MiddleEarthBattleApplication.class.getResource("styles/app-style.css").toExternalForm());
        FontService.applyApplicationFont(scene);

        BattleController controller = fxmlLoader.getController();

        Stage battleStage = new Stage();
        prepareStage(battleStage, title, scene, 1200, 760);

        battleStage.setOnHidden(event -> {
            controller.shutdown();
            openBattleModes.remove(battleMode);
            unregisterBattleController(controller);
        });

        MusicService.playBattleTheme();
    }

    private static void ensureSharedGameState() {
        if (sharedGameState == null) {
            sharedGameState = GameStateFactory.createDefaultGameState();
        }
    }

    private static void executeShutdownAction() {
        shutdownAction.run();
        clearShutdownAction();
    }

    private static void showScene(String fxmlFile, String title, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MiddleEarthBattleApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add(MiddleEarthBattleApplication.class.getResource("styles/app-style.css").toExternalForm());
        FontService.applyApplicationFont(scene);

        prepareStage(primaryStage, title, scene, 1100, 720);
    }

    private static void prepareStage(Stage stage, String title, Scene scene, double minWidth, double minHeight) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        stage.setTitle(title);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setResizable(true);
        stage.setScene(scene);

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.show();

        Platform.runLater(() -> {
            stage.setMaximized(true);
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        });
    }

    @Override
    public void stop() {
        executeShutdownAction();
        MusicService.stop();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}