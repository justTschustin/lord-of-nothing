package io.github.lord_of_nothing.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private final List<String> playlist;
    private int currentTrackIndex = 0;
    private Music currentMusic;
    private boolean isPlaying = false;
    private float masterVolume = 0.7f;

    public AudioManager() {
        this.playlist = new ArrayList<>();
        initializePlaylist();
    }

    /**
     * Initialisiert die Playlist mit allen 8 Soundtracks.
     */
    private void initializePlaylist() {
        for (int i = 1; i <= 8; i++) {
            playlist.add("music/Music_Loop_" + i + ".mp3");
        }
    }

    /**
     * Startet die Playlist vom ersten Track an.
     */
    public void startPlaylist() {
        if (isPlaying) {
            return; // Verhindert doppeltes Starten
        }
        currentTrackIndex = 0;
        isPlaying = true;
        playCurrentTrack();
    }

    /**
     * Spielt den aktuellen Track ab.
     */
    private void playCurrentTrack() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        String trackPath = playlist.get(currentTrackIndex);
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(trackPath));
        currentMusic.setVolume(masterVolume);

        // Listener: Wenn Track zu Ende → nächster Track
        currentMusic.setOnCompletionListener(music -> {
            currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
            playCurrentTrack();
        });

        currentMusic.play();
    }

    /**
     * Setzt die Master-Lautstärke (0.0 bis 1.0).
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(this.masterVolume);
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    /**
     * Stoppt die Musik.
     */
    public void stopPlaylist() {
        isPlaying = false;
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * Gibt alle Musik-Ressourcen frei.
     */
    public void dispose() {
        stopPlaylist();
        if (currentMusic != null) {
            currentMusic.dispose();
        }
    }
}
