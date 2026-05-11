package io.github.lord_of_nothing.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private final List<String> playlist;
    private int currentTrackIndex = 0;
    private Music currentMusic;
    private boolean isPlaying = false;
    private float masterVolume = 0.7f;
    private float musicVolume = 0.7f;
    private float soundVolume = 0.7f;
    private Sound assignSound, menuSound, placeSound, selectSound, unableSound;

    public AudioManager() {
        this.playlist = new ArrayList<>();
        initializePlaylist();
        assignSound = Gdx.audio.newSound(Gdx.files.internal("sounds/assign.mp3"));
        menuSound = Gdx.audio.newSound(Gdx.files.internal("sounds/menuClick.mp3"));
        placeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/place.mp3"));
        selectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select.mp3"));
        unableSound = Gdx.audio.newSound(Gdx.files.internal("sounds/unable.mp3"));    }

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
        currentMusic.setVolume(musicVolume);

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
    /**
     * <summary>Updates the music channel volume and applies it to the currently playing track.</summary>
     * @param volume Normalized volume level (0.0 to 1.0).
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(this.musicVolume);
        }
    }
    /**
     * <summary>Updates the sound effects channel volume for all future SFX playbacks.</summary>
     * @param volume Normalized volume level (0.0 to 1.0).
     */
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0, Math.min(1, volume));
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

    public void playAssign() { if (assignSound != null) assignSound.play(soundVolume); }
    public void playMenuClick() { if (menuSound != null) menuSound.play(soundVolume); }
    public void playPlace() { if (placeSound != null) placeSound.play(soundVolume); }
    public void playSelect() { if (selectSound != null) selectSound.play(soundVolume); }
    public void playUnable() { if (unableSound != null) unableSound.play(soundVolume); }

    /**
     * Frees all music resources
     */
    public void dispose() {
        stopPlaylist();
        if (currentMusic != null) {
            currentMusic.dispose();
        }
        Sound[] sounds = {assignSound, menuSound, placeSound, selectSound, unableSound};
        for (Sound s : sounds) if (s != null) s.dispose();
    }
}
