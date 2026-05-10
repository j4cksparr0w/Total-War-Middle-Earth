package com.marija.middleearthbattle.config;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameConfigurationService {

    private static final Path CONFIG_DIRECTORY = Path.of("config");
    private static final Path CONFIG_FILE = CONFIG_DIRECTORY.resolve("game-config.xml");

    public GameConfiguration loadOrCreateDefaultConfiguration() {
        try {
            if (Files.notExists(CONFIG_FILE)) {
                GameConfiguration defaultConfiguration = createDefaultConfiguration();
                saveConfiguration(defaultConfiguration);
                return defaultConfiguration;
            }

            return loadConfiguration();
        } catch (Exception exception) {
            return createDefaultConfiguration();
        }
    }

    public GameConfiguration loadConfiguration() throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        UnitConfigurationSaxHandler handler = new UnitConfigurationSaxHandler();

        saxParser.parse(CONFIG_FILE.toFile(), handler);

        return new GameConfiguration(handler.getUnits());
    }

    public void saveConfiguration(GameConfiguration gameConfiguration) throws ParserConfigurationException, TransformerException, IOException {
        Files.createDirectories(CONFIG_DIRECTORY);

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement("gameConfiguration");
        document.appendChild(rootElement);

        Element unitsElement = document.createElement("units");
        rootElement.appendChild(unitsElement);

        for (UnitConfiguration unitConfiguration : gameConfiguration.units()) {
            Element unitElement = document.createElement("unit");

            unitElement.setAttribute("name", unitConfiguration.name());
            unitElement.setAttribute("faction", unitConfiguration.faction().name());
            unitElement.setAttribute("unitType", unitConfiguration.unitType().name());
            unitElement.setAttribute("maxHealth", String.valueOf(unitConfiguration.maxHealth()));
            unitElement.setAttribute("attack", String.valueOf(unitConfiguration.attack()));
            unitElement.setAttribute("defense", String.valueOf(unitConfiguration.defense()));

            unitsElement.appendChild(unitElement);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(CONFIG_FILE.toFile());

        transformer.transform(source, result);
    }

    private GameConfiguration createDefaultConfiguration() {
        return new GameConfiguration(List.of(
                new UnitConfiguration("Gondor Commander", Faction.GONDOR, UnitType.COMMANDER, 100, 24, 8),
                new UnitConfiguration("Gondor Soldier", Faction.GONDOR, UnitType.INFANTRY, 80, 18, 10),
                new UnitConfiguration("Gondor Archer", Faction.GONDOR, UnitType.RANGED, 65, 21, 5),
                new UnitConfiguration("Gondor Cavalry", Faction.GONDOR, UnitType.CAVALRY, 90, 23, 7),
                new UnitConfiguration("Mordor Commander", Faction.MORDOR, UnitType.COMMANDER, 100, 24, 8),
                new UnitConfiguration("Orc Warrior", Faction.MORDOR, UnitType.INFANTRY, 80, 18, 10),
                new UnitConfiguration("Orc Archer", Faction.MORDOR, UnitType.RANGED, 65, 21, 5),
                new UnitConfiguration("Mordor Troll", Faction.MORDOR, UnitType.MONSTER, 150, 30, 4)
        ));
    }
}