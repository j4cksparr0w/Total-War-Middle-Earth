package com.marija.middleearthbattle.rmi;

import com.marija.middleearthbattle.model.Faction;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PlayerInfo(
        String playerName,
        Faction faction,
        LocalDateTime registeredAt
) implements Serializable {
}