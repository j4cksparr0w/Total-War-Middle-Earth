package com.marija.middleearthbattle.audio;

import com.marija.middleearthbattle.MiddleEarthBattleApplication;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public final class MusicService {

    private static MediaPlayer mediaPlayer;
    private static String currentTrack;
    private static boolean muted;

    private MusicService() {
    }

    public static void playMenuTheme() {
        play("menu_theme.mp3", 0.30);
    }

    public static void playBattleTheme() {
        play("battle_theme.mp3", 0.35);
    }

    public static void toggleMute() {
        muted = !muted;

        if (mediaPlayer != null) {
            mediaPlayer.setMute(muted);
        }
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            currentTrack = null;
        }
    }

    private static void play(String fileName, double volume) {
        URL resource = MiddleEarthBattleApplication.class.getResource("audio/" + fileName);

        if (resource == null) {
            return;
        }

        String track = resource.toExternalForm();

        if (track.equals(currentTrack) && mediaPlayer != null) {
            return;
        }

        stop();

        try {
            Media media = new Media(track);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(volume);
            mediaPlayer.setMute(muted);
            mediaPlayer.play();
            currentTrack = track;
        } catch (RuntimeException exception) {
            stop();
        }
    }
}