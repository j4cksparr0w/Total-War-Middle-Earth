package com.marija.middleearthbattle.ui;

import com.marija.middleearthbattle.MiddleEarthBattleApplication;
import javafx.scene.Scene;
import javafx.scene.text.Font;

import java.io.InputStream;

public final class FontService {

    private static final String FALLBACK_FONT = "Georgia";
    private static String applicationFontFamily;

    private FontService() {
    }

    public static void applyApplicationFont(Scene scene) {
        String fontFamily = getApplicationFontFamily();
        scene.getRoot().setStyle("-fx-font-family: '" + fontFamily + "';");
    }

    private static String getApplicationFontFamily() {
        if (applicationFontFamily != null) {
            return applicationFontFamily;
        }

        try (InputStream inputStream = MiddleEarthBattleApplication.class.getResourceAsStream("fonts/fantasy_font.ttf")) {
            if (inputStream == null) {
                applicationFontFamily = FALLBACK_FONT;
                return applicationFontFamily;
            }

            Font font = Font.loadFont(inputStream, 14);
            applicationFontFamily = font != null ? font.getFamily() : FALLBACK_FONT;

            return applicationFontFamily;
        } catch (Exception exception) {
            applicationFontFamily = FALLBACK_FONT;
            return applicationFontFamily;
        }
    }
}