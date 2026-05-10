package com.marija.middleearthbattle;

import com.marija.middleearthbattle.async.GameLoadingService;
import com.marija.middleearthbattle.audio.MusicService;
import com.marija.middleearthbattle.engine.AttackResult;
import com.marija.middleearthbattle.engine.BattleMoveService;
import com.marija.middleearthbattle.model.BattleMode;
import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.model.GameStatus;
import com.marija.middleearthbattle.model.Player;
import com.marija.middleearthbattle.model.UnitType;
import com.marija.middleearthbattle.model.unit.GameUnit;
import com.marija.middleearthbattle.move.GameMove;
import com.marija.middleearthbattle.move.GameMoveMapper;
import com.marija.middleearthbattle.network.GameMoveServer;
import com.marija.middleearthbattle.network.NetworkMoveService;
import com.marija.middleearthbattle.rmi.ChatMessage;
import com.marija.middleearthbattle.rmi.PlayerInfo;
import com.marija.middleearthbattle.rmi.RmiLobbyClient;
import com.marija.middleearthbattle.rmi.RmiLobbyServer;
import com.marija.middleearthbattle.save.SaveGameService;
import com.marija.middleearthbattle.ui.ImageService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import javax.naming.NamingException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BattleController {

    private static final String LOCALHOST = "localhost";
    private static final String REMOTE_HOST_PROPERTY = "middleearth.remote.host";
    private static final int GONDOR_TCP_PORT = 5555;
    private static final int MORDOR_TCP_PORT = 5556;
    private static final int RMI_PORT = 1099;
    private static final String RMI_SERVICE_NAME = "MiddleEarthLobby";
    private static final DateTimeFormatter CHAT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final BattleMoveService battleMoveService = new BattleMoveService();
    private final SaveGameService saveGameService = new SaveGameService();
    private final GameLoadingService gameLoadingService = new GameLoadingService();
    private final NetworkMoveService networkMoveService = new NetworkMoveService();

    private GameState gameState;
    private GameMoveServer gameMoveServer;
    private RmiLobbyServer rmiLobbyServer;
    private RmiLobbyClient rmiLobbyClient;
    private Timeline chatRefreshTimeline;
    private BattleMode battleMode;
    private final Set<UUID> sentTcpMoveIds = ConcurrentHashMap.newKeySet();
    private final Set<UUID> receivedTcpMoveIds = ConcurrentHashMap.newKeySet();

    @FXML
    private BorderPane battleRoot;

    @FXML
    private ImageView gondorCommanderImageView;

    @FXML
    private ImageView gondorSoldierImageView;

    @FXML
    private ImageView gondorArcherImageView;

    @FXML
    private ImageView gondorCavalryImageView;

    @FXML
    private ImageView mordorCommanderImageView;

    @FXML
    private ImageView orcWarriorImageView;

    @FXML
    private ImageView orcArcherImageView;

    @FXML
    private ImageView mordorTrollImageView;

    @FXML
    private Button musicToggleButton;

    @FXML
    private Label playerPerspectiveLabel;

    @FXML
    private Button endTurnButton;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField chatMessageTextField;

    @FXML
    private Label currentTurnLabel;

    @FXML
    private ComboBox<GameUnit> attackerComboBox;

    @FXML
    private ComboBox<GameUnit> defenderComboBox;

    @FXML
    private Button attackButton;

    @FXML
    private Label battleStatusLabel;

    @FXML
    private TextArea battleLogTextArea;

    @FXML
    private ProgressBar gondorCommanderHealthBar;

    @FXML
    private ProgressBar gondorInfantryHealthBar;

    @FXML
    private ProgressBar gondorRangedHealthBar;

    @FXML
    private ProgressBar gondorCavalryHealthBar;

    @FXML
    private ProgressBar mordorCommanderHealthBar;

    @FXML
    private ProgressBar mordorInfantryHealthBar;

    @FXML
    private ProgressBar mordorRangedHealthBar;

    @FXML
    private ProgressBar mordorMonsterHealthBar;

    @FXML
    private Label gondorCommanderHealthLabel;

    @FXML
    private Label gondorInfantryHealthLabel;

    @FXML
    private Label gondorRangedHealthLabel;

    @FXML
    private Label gondorCavalryHealthLabel;

    @FXML
    private Label mordorCommanderHealthLabel;

    @FXML
    private Label mordorInfantryHealthLabel;

    @FXML
    private Label mordorRangedHealthLabel;

    @FXML
    private Label mordorMonsterHealthLabel;

    @FXML
    private void initialize() {
        battleMode = MiddleEarthBattleApplication.getSelectedBattleMode();

        updateMusicToggleButton();
        configureBackgroundImage();
        configureUnitImages();
        configureUnitComboBoxes();

        battleLogTextArea.setText("Preparing the battlefield...");
        setBattleControlsDisabled(true);

        if (MiddleEarthBattleApplication.isSharedBattleSession()) {
            gameState = MiddleEarthBattleApplication.getSharedGameState();
            MiddleEarthBattleApplication.registerBattleController(this);
            battleLogTextArea.setText(MiddleEarthBattleApplication.getSharedBattleLogText());
            setBattleControlsDisabled(false);
            refreshBattleScreen();
            startTcpServer();
            startRmiLobby();
            MiddleEarthBattleApplication.setShutdownAction(this::stopNetworking);
            return;
        }

        loadGameStateAsync();
    }

    private void configureBackgroundImage() {
        ImageService.setBackgroundImageIfExists(battleRoot, "backgrounds/battle_background.jpg");
    }

    private void configureUnitImages() {
        ImageService.setImage(gondorCommanderImageView, "units/gondor_commander.png");
        ImageService.setImage(gondorSoldierImageView, "units/gondor_soldier.png");
        ImageService.setImage(gondorArcherImageView, "units/gondor_archer.png");
        ImageService.setImage(gondorCavalryImageView, "units/gondor_cavalry.png");

        ImageService.setImage(mordorCommanderImageView, "units/mordor_commander.png");
        ImageService.setImage(orcWarriorImageView, "units/orc_warrior.png");
        ImageService.setImage(orcArcherImageView, "units/orc_archer.png");
        ImageService.setImage(mordorTrollImageView, "units/mordor_troll.png");
    }

    private void configureUnitComboBoxes() {
        StringConverter<GameUnit> unitStringConverter = new StringConverter<>() {
            @Override
            public String toString(GameUnit unit) {
                if (unit == null) {
                    return "";
                }

                return unit.getName() + " - " + unit.getHealth() + "/" + unit.getMaxHealth() + " HP";
            }

            @Override
            public GameUnit fromString(String value) {
                return null;
            }
        };

        attackerComboBox.setConverter(unitStringConverter);
        defenderComboBox.setConverter(unitStringConverter);
    }

    private void loadGameStateAsync() {
        gameLoadingService.setOnSucceeded(event -> {
            battleStatusLabel.textProperty().unbind();

            GameState pendingGameState = MiddleEarthBattleApplication.consumePendingInitialGameState();
            gameState = pendingGameState != null ? pendingGameState : gameLoadingService.getValue();

            battleLogTextArea.setText(pendingGameState != null
                    ? "Saved game has been loaded from the main menu."
                    : "The armies of Gondor and Mordor are ready for battle.");

            setBattleControlsDisabled(false);
            refreshBattleScreen();
            startTcpServer();
            startRmiLobby();
            MiddleEarthBattleApplication.setShutdownAction(this::stopNetworking);
        });

        gameLoadingService.setOnFailed(event -> {
            battleStatusLabel.textProperty().unbind();
            battleStatusLabel.setText("Game configuration could not be loaded.");
            battleLogTextArea.setText("The battlefield could not be prepared.");
            setBattleControlsDisabled(true);
        });

        battleStatusLabel.textProperty().bind(gameLoadingService.messageProperty());
        gameLoadingService.start();
    }

    private void setBattleControlsDisabled(boolean disabled) {
        attackButton.setDisable(disabled);
        endTurnButton.setDisable(disabled);
        attackerComboBox.setDisable(disabled);
        defenderComboBox.setDisable(disabled);
    }

    @FXML
    private void handleAttack() {
        try {
            GameUnit attacker = attackerComboBox.getValue();
            GameUnit defender = defenderComboBox.getValue();

            validateSelectedUnits(attacker, defender);

            GameMove gameMove = GameMoveMapper.fromUnits(attacker, defender);

            if (MiddleEarthBattleApplication.isSharedBattleSession()) {
                sendMoveThroughTcp(gameMove);
                appendBattleLog("TCP move sent to opponent: " + formatMove(gameMove));
                return;
            }

            AttackResult result = battleMoveService.executeSelectedMove(gameState, attacker, defender);
            appendBattleLog(result.message());
            appendBattleLog("Move prepared for TCP networking: " + formatMove(gameMove));
            sendMoveThroughTcp(gameMove);

            refreshBattleScreen();
        } catch (IllegalArgumentException exception) {
            battleStatusLabel.setText(exception.getMessage());
        }
    }

    private void validateSelectedUnits(GameUnit attacker, GameUnit defender) {
        if (attacker == null) {
            throw new IllegalArgumentException("Please choose an attacking unit.");
        }

        if (defender == null) {
            throw new IllegalArgumentException("Please choose a target unit.");
        }
    }

    @FXML
    private void handleEndTurn() {
        if (isBattleFinished()) {
            battleStatusLabel.setText("The battle is already finished.");
            return;
        }

        String previousFaction = gameState.getCurrentTurn().getDisplayName();

        if (gameState.getStatus() == GameStatus.READY) {
            gameState.setStatus(GameStatus.IN_PROGRESS);
        }

        gameState.switchTurn();

        if (MiddleEarthBattleApplication.isSharedBattleSession()) {
            MiddleEarthBattleApplication.appendSharedBattleLog(previousFaction + " ended the turn.");
        } else {
            appendBattleLog(previousFaction + " ended the turn.");
        }

        refreshBattleScreen();
        notifySharedBattleWindows();
    }

    @FXML
    private void handleSaveGame() {
        try {
            saveGameService.save(gameState);
            battleStatusLabel.setText("Game has been saved.");

            if (MiddleEarthBattleApplication.isSharedBattleSession()) {
                MiddleEarthBattleApplication.appendSharedBattleLog("Game state has been saved.");
                notifySharedBattleWindows();
            } else {
                appendBattleLog("Game state has been saved.");
            }
        } catch (IOException exception) {
            battleStatusLabel.setText("Game could not be saved.");
        }
    }

    @FXML
    private void handleLoadGame() {
        try {
            if (!saveGameService.saveFileExists()) {
                battleStatusLabel.setText("There is no saved game yet.");
                return;
            }

            gameState = saveGameService.load();

            if (MiddleEarthBattleApplication.isSharedBattleSession()) {
                MiddleEarthBattleApplication.replaceSharedGameState(gameState);
                MiddleEarthBattleApplication.appendSharedBattleLog("Saved game has been loaded.");
                battleLogTextArea.setText(MiddleEarthBattleApplication.getSharedBattleLogText());
            } else {
                battleLogTextArea.setText("Saved game has been loaded.");
            }

            refreshBattleScreen();
            notifySharedBattleWindows();
        } catch (IOException | ClassNotFoundException exception) {
            battleStatusLabel.setText("Game could not be loaded.");
        }
    }

    @FXML
    private void handleBackToMenu() {
        if (MiddleEarthBattleApplication.isSharedBattleSession()) {
            closeSharedBattleWindow();
            return;
        }

        try {
            stopNetworking();
            MiddleEarthBattleApplication.showMainMenu();
        } catch (IOException exception) {
            battleStatusLabel.setText("Main menu could not be loaded.");
        }
    }

    @FXML
    private void handleSendChatMessage() {
        if (rmiLobbyClient == null) {
            battleStatusLabel.setText("RMI chat service is not available.");
            return;
        }

        try {
            String message = chatMessageTextField.getText();

            if (message == null || message.isBlank()) {
                return;
            }

            rmiLobbyClient.sendMessage(resolveLocalPlayerName(), message);
            chatMessageTextField.clear();

            refreshChatMessages();
            notifySharedBattleWindows();
        } catch (RemoteException exception) {
            battleStatusLabel.setText("Chat message could not be sent.");
        }
    }

    @FXML
    private void handleToggleMusic() {
        MusicService.toggleMute();
        updateMusicToggleButton();
    }

    private void refreshBattleScreen() {
        currentTurnLabel.setText("Current turn: " + gameState.getCurrentTurn().getDisplayName());
        refreshPlayerPerspectiveLabel();
        refreshSelectableUnits();
        refreshHealthPanels();
        refreshBattleStatus();
    }

    private void refreshPlayerPerspectiveLabel() {
        if (battleMode == BattleMode.GONDOR_PLAYER) {
            playerPerspectiveLabel.setText("You are playing as Gondor");
            return;
        }

        if (battleMode == BattleMode.MORDOR_PLAYER) {
            playerPerspectiveLabel.setText("You are playing as Mordor");
            return;
        }

        playerPerspectiveLabel.setText("Mode: Local test");
    }

    private void refreshSelectableUnits() {
        Player attackerPlayer = getAttackerSelectionPlayer();
        Player defenderPlayer = getDefenderSelectionPlayer();

        ObservableList<GameUnit> attackers = getLivingUnits(attackerPlayer);
        ObservableList<GameUnit> defenders = getLivingUnits(defenderPlayer);

        attackerComboBox.setItems(attackers);
        defenderComboBox.setItems(defenders);

        if (!attackers.isEmpty()) {
            attackerComboBox.getSelectionModel().selectFirst();
        }

        if (!defenders.isEmpty()) {
            defenderComboBox.getSelectionModel().selectFirst();
        }
    }

    private Player getAttackerSelectionPlayer() {
        if (battleMode == BattleMode.GONDOR_PLAYER) {
            return gameState.getGondorPlayer();
        }

        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return gameState.getMordorPlayer();
        }

        return gameState.getCurrentPlayer();
    }

    private Player getDefenderSelectionPlayer() {
        if (battleMode == BattleMode.GONDOR_PLAYER) {
            return gameState.getMordorPlayer();
        }

        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return gameState.getGondorPlayer();
        }

        return gameState.getOpponentPlayer();
    }

    private ObservableList<GameUnit> getLivingUnits(Player player) {
        return FXCollections.observableArrayList(
                player.getUnits()
                        .stream()
                        .filter(GameUnit::isAlive)
                        .toList()
        );
    }

    private void refreshHealthPanels() {
        updateUnitHealth(findUnit(gameState.getGondorPlayer(), UnitType.COMMANDER), gondorCommanderHealthBar, gondorCommanderHealthLabel);
        updateUnitHealth(findUnit(gameState.getGondorPlayer(), UnitType.INFANTRY), gondorInfantryHealthBar, gondorInfantryHealthLabel);
        updateUnitHealth(findUnit(gameState.getGondorPlayer(), UnitType.RANGED), gondorRangedHealthBar, gondorRangedHealthLabel);
        updateUnitHealth(findUnit(gameState.getGondorPlayer(), UnitType.CAVALRY), gondorCavalryHealthBar, gondorCavalryHealthLabel);

        updateUnitHealth(findUnit(gameState.getMordorPlayer(), UnitType.COMMANDER), mordorCommanderHealthBar, mordorCommanderHealthLabel);
        updateUnitHealth(findUnit(gameState.getMordorPlayer(), UnitType.INFANTRY), mordorInfantryHealthBar, mordorInfantryHealthLabel);
        updateUnitHealth(findUnit(gameState.getMordorPlayer(), UnitType.RANGED), mordorRangedHealthBar, mordorRangedHealthLabel);
        updateUnitHealth(findUnit(gameState.getMordorPlayer(), UnitType.MONSTER), mordorMonsterHealthBar, mordorMonsterHealthLabel);
    }

    private GameUnit findUnit(Player player, UnitType unitType) {
        return player.getUnits()
                .stream()
                .filter(unit -> unit.getUnitType() == unitType)
                .findFirst()
                .orElseThrow();
    }

    private void updateUnitHealth(GameUnit unit, ProgressBar healthBar, Label healthLabel) {
        healthBar.setProgress(unit.getHealthPercentage());

        if (unit.isAlive()) {
            healthLabel.setText(unit.getHealth() + "/" + unit.getMaxHealth() + " HP");
        } else {
            healthLabel.setText("Defeated");
        }
    }

    private void refreshBattleStatus() {
        boolean battleFinished = isBattleFinished();
        boolean playerTurn = isCurrentLocalPlayerTurn();

        attackButton.setDisable(battleFinished || !playerTurn);
        endTurnButton.setDisable(battleFinished || !playerTurn);
        attackerComboBox.setDisable(battleFinished || !playerTurn);
        defenderComboBox.setDisable(battleFinished || !playerTurn);

        if (gameState.getStatus() == GameStatus.GONDOR_WON) {
            battleStatusLabel.setText("Gondor has won the battle.");
            return;
        }

        if (gameState.getStatus() == GameStatus.MORDOR_WON) {
            battleStatusLabel.setText("Mordor has won the battle.");
            return;
        }

        if (!playerTurn) {
            battleStatusLabel.setText("Waiting for " + gameState.getCurrentTurn().getDisplayName() + " to play.");
            return;
        }

        battleStatusLabel.setText(gameState.getCurrentTurn().getDisplayName() + " may attack.");
    }

    private boolean isCurrentLocalPlayerTurn() {
        if (battleMode == BattleMode.LOCAL_TEST) {
            return true;
        }

        if (battleMode == BattleMode.GONDOR_PLAYER) {
            return gameState.getCurrentTurn() == Faction.GONDOR;
        }

        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return gameState.getCurrentTurn() == Faction.MORDOR;
        }

        return false;
    }

    private boolean isBattleFinished() {
        return gameState.getStatus() == GameStatus.GONDOR_WON || gameState.getStatus() == GameStatus.MORDOR_WON;
    }

    private void startTcpServer() {
        if (gameMoveServer != null) {
            return;
        }

        int localPort = resolveLocalTcpPort();

        gameMoveServer = new GameMoveServer(
                localPort,
                gameMove -> Platform.runLater(() -> handleReceivedTcpMove(gameMove)),
                message -> Platform.runLater(() -> appendBattleLog(message))
        );

        gameMoveServer.start();
    }

    private void handleReceivedTcpMove(GameMove gameMove) {
        if (sentTcpMoveIds.remove(gameMove.id())) {
            appendBattleLog("TCP confirmed sent move: " + formatMove(gameMove));
            return;
        }

        if (!receivedTcpMoveIds.add(gameMove.id())) {
            appendBattleLog("Duplicate TCP move ignored: " + formatMove(gameMove));
            return;
        }

        try {
            AttackResult result = battleMoveService.executeNetworkMove(gameState, gameMove);

            if (MiddleEarthBattleApplication.isSharedBattleSession()) {
                MiddleEarthBattleApplication.appendSharedBattleLog("TCP received move: " + formatMove(gameMove));
                MiddleEarthBattleApplication.appendSharedBattleLog(result.message());
                battleLogTextArea.setText(MiddleEarthBattleApplication.getSharedBattleLogText());
                notifySharedBattleWindows();
            } else {
                appendBattleLog("TCP received move: " + formatMove(gameMove));
                appendBattleLog(result.message());
            }

            refreshBattleScreen();
        } catch (IllegalArgumentException exception) {
            appendBattleLog("TCP move ignored: " + exception.getMessage());
        }
    }

    private void sendMoveThroughTcp(GameMove gameMove) {
        int targetPort = resolveTargetTcpPort();
        sentTcpMoveIds.add(gameMove.id());

        networkMoveService.sendMove(
                resolveRemoteHost(),
                targetPort,
                gameMove,
                sentMove -> Platform.runLater(() -> appendBattleLog("TCP sent move: " + formatMove(sentMove))),
                throwable -> Platform.runLater(() -> {
                    sentTcpMoveIds.remove(gameMove.id());
                    appendBattleLog("TCP move could not be sent.");
                })
        );
    }

    private String resolveRemoteHost() {
        String configuredHost = System.getProperty(REMOTE_HOST_PROPERTY);

        if (configuredHost == null || configuredHost.isBlank()) {
            return LOCALHOST;
        }

        return configuredHost.trim();
    }

    private String resolveRmiHost() {
        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return resolveRemoteHost();
        }

        return LOCALHOST;
    }

    private int resolveLocalTcpPort() {
        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return MORDOR_TCP_PORT;
        }

        return GONDOR_TCP_PORT;
    }

    private int resolveTargetTcpPort() {
        if (battleMode == BattleMode.GONDOR_PLAYER) {
            return MORDOR_TCP_PORT;
        }

        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return GONDOR_TCP_PORT;
        }

        return GONDOR_TCP_PORT;
    }

    private String formatMove(GameMove gameMove) {
        return gameMove.attackerFaction() + " " + gameMove.attackerUnitType() + " -> " + gameMove.defenderUnitType();
    }

    private void startRmiLobby() {
        try {
            if (battleMode == BattleMode.LOCAL_TEST || battleMode == BattleMode.GONDOR_PLAYER) {
                rmiLobbyServer = new RmiLobbyServer(RMI_PORT, RMI_SERVICE_NAME);
                rmiLobbyServer.start();
            }

            rmiLobbyClient = new RmiLobbyClient(resolveRmiHost(), RMI_PORT, RMI_SERVICE_NAME);

            if (battleMode == BattleMode.LOCAL_TEST) {
                rmiLobbyClient.registerPlayer(gameState.getGondorPlayer().getName(), Faction.GONDOR);
                rmiLobbyClient.registerPlayer(gameState.getMordorPlayer().getName(), Faction.MORDOR);
            } else {
                rmiLobbyClient.registerPlayer(resolveLocalPlayerName(), resolveLocalFaction());
            }

            appendBattleLog("RMI lobby and chat service is available.");
            refreshLobbyPlayers();
            refreshChatMessages();
            startChatAutoRefresh();
        } catch (RemoteException | NamingException exception) {
            rmiLobbyClient = null;
            appendBattleLog("RMI lobby and chat service could not be started. Open Host Game before Join Game.");
        }
    }

    private void refreshLobbyPlayers() {
        if (rmiLobbyClient == null) {
            return;
        }

        try {
            List<PlayerInfo> players = rmiLobbyClient.getPlayers();

            String playerNames = players.stream()
                    .map(player -> player.playerName() + " as " + player.faction().getDisplayName())
                    .collect(Collectors.joining(", "));

            appendBattleLog("RMI lobby players: " + playerNames);
        } catch (RemoteException exception) {
            appendBattleLog("RMI lobby players could not be loaded.");
        }
    }

    private void refreshChatMessages() {
        if (rmiLobbyClient == null) {
            return;
        }

        try {
            List<ChatMessage> messages = rmiLobbyClient.getMessages();

            String chatText = messages.stream()
                    .map(message -> CHAT_TIME_FORMATTER.format(message.sentAt()) + " " + message.senderName() + ": " + message.content())
                    .collect(Collectors.joining(System.lineSeparator()));

            chatTextArea.setText(chatText);
        } catch (RemoteException exception) {
            battleStatusLabel.setText("Chat messages could not be loaded.");
        }
    }

    private void startChatAutoRefresh() {
        stopChatAutoRefresh();

        chatRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshChatMessages()));
        chatRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        chatRefreshTimeline.play();
    }

    private void stopChatAutoRefresh() {
        if (chatRefreshTimeline != null) {
            chatRefreshTimeline.stop();
            chatRefreshTimeline = null;
        }
    }

    private String resolveLocalPlayerName() {
        if (battleMode == BattleMode.GONDOR_PLAYER) {
            return gameState.getGondorPlayer().getName();
        }

        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return gameState.getMordorPlayer().getName();
        }

        return gameState.getCurrentPlayer().getName();
    }

    private Faction resolveLocalFaction() {
        if (battleMode == BattleMode.MORDOR_PLAYER) {
            return Faction.MORDOR;
        }

        return Faction.GONDOR;
    }

    private void stopRmiLobby() {
        if (rmiLobbyServer != null) {
            rmiLobbyServer.stop();
            rmiLobbyServer = null;
        }

        rmiLobbyClient = null;
    }

    public void refreshSharedBattleWindow() {
        if (MiddleEarthBattleApplication.isSharedBattleSession()) {
            gameState = MiddleEarthBattleApplication.getSharedGameState();
            battleLogTextArea.setText(MiddleEarthBattleApplication.getSharedBattleLogText());
        }

        if (gameState != null) {
            refreshBattleScreen();
            refreshChatMessages();
        }
    }

    private void notifySharedBattleWindows() {
        if (MiddleEarthBattleApplication.isSharedBattleSession()) {
            MiddleEarthBattleApplication.refreshSharedBattleWindows();
        }
    }

    public void shutdown() {
        stopNetworking();
    }

    private void closeSharedBattleWindow() {
        stopNetworking();
        MiddleEarthBattleApplication.unregisterBattleController(this);
        Stage stage = (Stage) battleStatusLabel.getScene().getWindow();
        stage.close();
    }

    private void updateMusicToggleButton() {
        musicToggleButton.setText(MusicService.isMuted() ? "Music Off" : "Music On");
    }

    private void stopNetworking() {
        stopChatAutoRefresh();

        try {
            if (gameMoveServer != null) {
                gameMoveServer.stop();
                gameMoveServer = null;
            }
        } catch (RuntimeException exception) {
            appendBattleLog("TCP server shutdown finished with a warning.");
        }

        try {
            stopRmiLobby();
        } catch (RuntimeException exception) {
            appendBattleLog("RMI lobby shutdown finished with a warning.");
        }

        networkMoveService.shutdown();
        MiddleEarthBattleApplication.clearShutdownAction();
    }

    private void appendBattleLog(String message) {
        battleLogTextArea.appendText(System.lineSeparator() + message);
    }
}