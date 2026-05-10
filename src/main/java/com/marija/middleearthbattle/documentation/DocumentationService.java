package com.marija.middleearthbattle.documentation;

import com.marija.middleearthbattle.BattleController;
import com.marija.middleearthbattle.MainMenuController;
import com.marija.middleearthbattle.MiddleEarthBattleApplication;
import com.marija.middleearthbattle.async.GameLoadingService;
import com.marija.middleearthbattle.audio.MusicService;
import com.marija.middleearthbattle.config.GameConfiguration;
import com.marija.middleearthbattle.config.GameConfigurationService;
import com.marija.middleearthbattle.config.UnitConfiguration;
import com.marija.middleearthbattle.config.UnitConfigurationSaxHandler;
import com.marija.middleearthbattle.engine.AttackResult;
import com.marija.middleearthbattle.engine.BattleMoveService;
import com.marija.middleearthbattle.engine.GameEngine;
import com.marija.middleearthbattle.engine.GameStateFactory;
import com.marija.middleearthbattle.model.BattleMode;
import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.GameState;
import com.marija.middleearthbattle.model.GameStatus;
import com.marija.middleearthbattle.model.Player;
import com.marija.middleearthbattle.model.UnitType;
import com.marija.middleearthbattle.model.unit.CavalryUnit;
import com.marija.middleearthbattle.model.unit.CommanderUnit;
import com.marija.middleearthbattle.model.unit.GameUnit;
import com.marija.middleearthbattle.model.unit.InfantryUnit;
import com.marija.middleearthbattle.model.unit.MonsterUnit;
import com.marija.middleearthbattle.model.unit.RangedUnit;
import com.marija.middleearthbattle.move.GameMove;
import com.marija.middleearthbattle.move.GameMoveMapper;
import com.marija.middleearthbattle.network.GameMoveClient;
import com.marija.middleearthbattle.network.GameMoveServer;
import com.marija.middleearthbattle.network.NetworkMoveService;
import com.marija.middleearthbattle.rmi.ChatMessage;
import com.marija.middleearthbattle.rmi.GameLobbyRemoteService;
import com.marija.middleearthbattle.rmi.GameLobbyRemoteServiceImpl;
import com.marija.middleearthbattle.rmi.PlayerInfo;
import com.marija.middleearthbattle.rmi.RmiLobbyClient;
import com.marija.middleearthbattle.rmi.RmiLobbyServer;
import com.marija.middleearthbattle.save.SaveGameMapper;
import com.marija.middleearthbattle.save.SaveGameService;
import com.marija.middleearthbattle.save.SavedGameState;
import com.marija.middleearthbattle.save.SavedPlayer;
import com.marija.middleearthbattle.save.SavedUnit;
import com.marija.middleearthbattle.ui.FontService;
import com.marija.middleearthbattle.ui.ImageService;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DocumentationService {

    private static final Path DOCUMENTATION_DIRECTORY = Path.of("documentation");
    private static final Path DOCUMENTATION_FILE = DOCUMENTATION_DIRECTORY.resolve("generated-documentation.txt");

    private static final List<Class<?>> DOCUMENTED_CLASSES = List.of(
            MiddleEarthBattleApplication.class,
            MainMenuController.class,
            BattleController.class,

            GameLoadingService.class,
            MusicService.class,
            FontService.class,
            ImageService.class,

            GameEngine.class,
            BattleMoveService.class,
            GameStateFactory.class,
            AttackResult.class,

            GameMove.class,
            GameMoveMapper.class,

            GameMoveClient.class,
            GameMoveServer.class,
            NetworkMoveService.class,

            GameLobbyRemoteService.class,
            GameLobbyRemoteServiceImpl.class,
            RmiLobbyServer.class,
            RmiLobbyClient.class,
            PlayerInfo.class,
            ChatMessage.class,

            GameState.class,
            Player.class,
            BattleMode.class,
            Faction.class,
            UnitType.class,
            GameStatus.class,

            GameUnit.class,
            CommanderUnit.class,
            InfantryUnit.class,
            RangedUnit.class,
            CavalryUnit.class,
            MonsterUnit.class,

            GameConfiguration.class,
            UnitConfiguration.class,
            GameConfigurationService.class,
            UnitConfigurationSaxHandler.class,

            SaveGameService.class,
            SaveGameMapper.class,
            SavedGameState.class,
            SavedPlayer.class,
            SavedUnit.class
    );

    public Path generateDocumentation() throws IOException {
        Files.createDirectories(DOCUMENTATION_DIRECTORY);

        StringBuilder documentation = new StringBuilder();

        documentation.append("Middle-earth Battle Documentation").append(System.lineSeparator());
        documentation.append("Generated at: ").append(LocalDateTime.now()).append(System.lineSeparator());
        documentation.append("=".repeat(80)).append(System.lineSeparator()).append(System.lineSeparator());

        for (Class<?> documentedClass : DOCUMENTED_CLASSES) {
            appendClassDocumentation(documentation, documentedClass);
        }

        Files.writeString(DOCUMENTATION_FILE, documentation.toString());

        return DOCUMENTATION_FILE;
    }

    private void appendClassDocumentation(StringBuilder documentation, Class<?> documentedClass) {
        documentation.append("Class: ").append(documentedClass.getName()).append(System.lineSeparator());
        documentation.append("Type: ").append(resolveClassType(documentedClass)).append(System.lineSeparator());

        Class<?> superclass = documentedClass.getSuperclass();

        if (superclass != null) {
            documentation.append("Superclass: ").append(superclass.getName()).append(System.lineSeparator());
        }

        appendRecordComponents(documentation, documentedClass);
        appendFields(documentation, documentedClass);
        appendConstructors(documentation, documentedClass);
        appendMethods(documentation, documentedClass);

        documentation.append("-".repeat(80)).append(System.lineSeparator()).append(System.lineSeparator());
    }

    private String resolveClassType(Class<?> documentedClass) {
        if (documentedClass.isEnum()) {
            return "Enum";
        }

        if (documentedClass.isRecord()) {
            return "Record";
        }

        if (documentedClass.isInterface()) {
            return "Interface";
        }

        if (Modifier.isAbstract(documentedClass.getModifiers())) {
            return "Abstract class";
        }

        return "Class";
    }

    private void appendRecordComponents(StringBuilder documentation, Class<?> documentedClass) {
        if (!documentedClass.isRecord()) {
            return;
        }

        documentation.append("Record components:").append(System.lineSeparator());

        RecordComponent[] recordComponents = documentedClass.getRecordComponents();

        Arrays.stream(recordComponents)
                .sorted(Comparator.comparing(RecordComponent::getName))
                .forEach(component -> documentation
                        .append("  ")
                        .append(component.getType().getSimpleName())
                        .append(" ")
                        .append(component.getName())
                        .append(System.lineSeparator()));
    }

    private void appendFields(StringBuilder documentation, Class<?> documentedClass) {
        Field[] fields = documentedClass.getDeclaredFields();

        if (fields.length == 0) {
            return;
        }

        documentation.append("Fields:").append(System.lineSeparator());

        Arrays.stream(fields)
                .sorted(Comparator.comparing(Field::getName))
                .forEach(field -> documentation
                        .append("  ")
                        .append(Modifier.toString(field.getModifiers()))
                        .append(" ")
                        .append(field.getType().getSimpleName())
                        .append(" ")
                        .append(field.getName())
                        .append(System.lineSeparator()));
    }

    private void appendConstructors(StringBuilder documentation, Class<?> documentedClass) {
        Constructor<?>[] constructors = documentedClass.getDeclaredConstructors();

        if (constructors.length == 0) {
            return;
        }

        documentation.append("Constructors:").append(System.lineSeparator());

        Arrays.stream(constructors)
                .sorted(Comparator.comparing(Constructor::getParameterCount))
                .forEach(constructor -> documentation
                        .append("  ")
                        .append(Modifier.toString(constructor.getModifiers()))
                        .append(" ")
                        .append(documentedClass.getSimpleName())
                        .append("(")
                        .append(formatParameterTypes(constructor.getParameterTypes()))
                        .append(")")
                        .append(System.lineSeparator()));
    }

    private void appendMethods(StringBuilder documentation, Class<?> documentedClass) {
        Method[] methods = documentedClass.getDeclaredMethods();

        if (methods.length == 0) {
            return;
        }

        documentation.append("Methods:").append(System.lineSeparator());

        Arrays.stream(methods)
                .sorted(Comparator.comparing(Method::getName))
                .forEach(method -> documentation
                        .append("  ")
                        .append(Modifier.toString(method.getModifiers()))
                        .append(" ")
                        .append(method.getReturnType().getSimpleName())
                        .append(" ")
                        .append(method.getName())
                        .append("(")
                        .append(formatParameterTypes(method.getParameterTypes()))
                        .append(")")
                        .append(System.lineSeparator()));
    }

    private String formatParameterTypes(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(Class::getSimpleName)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }
}