package com.marija.middleearthbattle.model.unit;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.UnitType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class GameUnit {

    private final StringProperty name;
    private final Faction faction;
    private final UnitType unitType;
    private final IntegerProperty health;
    private final IntegerProperty maxHealth;
    private final IntegerProperty attack;
    private final IntegerProperty defense;
    private final BooleanProperty alive;

    protected GameUnit(String name, Faction faction, UnitType unitType, int maxHealth, int attack, int defense) {
        this.name = new SimpleStringProperty(name);
        this.faction = faction;
        this.unitType = unitType;
        this.health = new SimpleIntegerProperty(maxHealth);
        this.maxHealth = new SimpleIntegerProperty(maxHealth);
        this.attack = new SimpleIntegerProperty(attack);
        this.defense = new SimpleIntegerProperty(defense);
        this.alive = new SimpleBooleanProperty(true);
    }

    public void receiveDamage(int damage) {
        int newHealth = Math.max(0, getHealth() - damage);
        health.set(newHealth);
        alive.set(newHealth > 0);
    }


    public double getHealthPercentage() {
        if (getMaxHealth() == 0) {
            return 0;
        }

        return (double) getHealth() / getMaxHealth();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Faction getFaction() {
        return faction;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public int getHealth() {
        return health.get();
    }

    public IntegerProperty healthProperty() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth.get();
    }

    public IntegerProperty maxHealthProperty() {
        return maxHealth;
    }

    public int getAttack() {
        return attack.get();
    }

    public IntegerProperty attackProperty() {
        return attack;
    }

    public int getDefense() {
        return defense.get();
    }

    public IntegerProperty defenseProperty() {
        return defense;
    }

    public boolean isAlive() {
        return alive.get();
    }

    public BooleanProperty aliveProperty() {
        return alive;
    }

    public void restoreHealth(int restoredHealth) {
        int newHealth = Math.max(0, Math.min(restoredHealth, getMaxHealth()));
        health.set(newHealth);
        alive.set(newHealth > 0);
    }

    @Override
    public String toString() {
        return getName() + " - " + getHealth() + "/" + getMaxHealth() + " HP";
    }
}