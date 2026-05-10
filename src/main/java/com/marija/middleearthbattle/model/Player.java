package com.marija.middleearthbattle.model;

import com.marija.middleearthbattle.model.unit.GameUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Player {

    private final String name;
    private final Faction faction;
    private final ObservableList<GameUnit> units;

    public Player(String name, Faction faction) {
        this.name = name;
        this.faction = faction;
        this.units = FXCollections.observableArrayList();
    }

    public boolean hasLivingUnits() {
        return units.stream().anyMatch(GameUnit::isAlive);
    }

    public String getName() {
        return name;
    }

    public Faction getFaction() {
        return faction;
    }

    public ObservableList<GameUnit> getUnits() {
        return units;
    }
}