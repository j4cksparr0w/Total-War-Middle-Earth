package com.marija.middleearthbattle.save;

import com.marija.middleearthbattle.model.Faction;
import com.marija.middleearthbattle.model.GameStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record SavedGameState(
        SavedPlayer gondorPlayer,
        SavedPlayer mordorPlayer,
        Faction currentTurn,
        GameStatus status,
        LocalDateTime savedAt
) implements Serializable {
}