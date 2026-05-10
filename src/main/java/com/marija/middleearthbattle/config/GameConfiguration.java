package com.marija.middleearthbattle.config;

import java.util.List;

public record GameConfiguration(
        List<UnitConfiguration> units
) {
}