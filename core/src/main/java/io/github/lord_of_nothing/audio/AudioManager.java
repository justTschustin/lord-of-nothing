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
     * Initializes Playlist with all 8 soundtracks [CHANGE FOR-LOOP, IF MORE SONGS ARE ADDED!!!]
     */
    private void initializePlaylist() {
        for (int i = 1; i <= 8; i++) {
            playlist.add("music/Music_Loop_" + i + ".mp3");
        }
    }

    /**
     * Starts Playlist with first track
     */
    public void startPlaylist() {
        if (isPlaying) {
            return; // Prevents starting twice
        }
        currentTrackIndex = 0;
        isPlaying = true;
        playCurrentTrack();
    }

    /**
     * Plays current track
     */
    private void playCurrentTrack() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        String trackPath = playlist.get(currentTrackIndex);
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(trackPath));
        currentMusic.setVolume(masterVolume);

        // Listener: If Track ended -> next Track
        currentMusic.setOnCompletionListener(music -> {
            currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
            playCurrentTrack();
        });

        currentMusic.play();
    }

    /**
     * Sets master volume (0.0 ... 1.0)
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
     * Stops music
     */
    public void stopPlaylist() {
        isPlaying = false;
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * Frees all music resources
     */
    public void dispose() {
        stopPlaylist();
        if (currentMusic != null) {
            currentMusic.dispose();
        }
    }
}
