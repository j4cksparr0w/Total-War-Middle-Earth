package com.marija.middleearthbattle.config;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class UnitConfigurationSaxHandler extends DefaultHandler {

    private final List<UnitConfiguration> units = new ArrayList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (!"unit".equals(qName)) {
            return;
        }

        UnitConfiguration unitConfiguration = new UnitConfiguration(
                attributes.getValue("name"),
                Faction.valueOf(attributes.getValue("faction")),
                UnitType.valueOf(attributes.getValue("unitType")),
                Integer.parseInt(attributes.getValue("maxHealth")),
                Integer.parseInt(attributes.getValue("attack")),
                Integer.parseInt(attributes.getValue("defense"))
        );

        units.add(unitConfiguration);
    }

    public List<UnitConfiguration> getUnits() {
        return List.copyOf(units);
    }
}