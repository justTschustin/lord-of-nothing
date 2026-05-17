package io.github.lord_of_nothing.settings;

/**
 * Serializable game settings for display mode and window size.
 */
public class GameSettings {
    /** Whether fullscreen mode is enabled. */
    public boolean fullscreen = false;

    /** Last known windowed width in pixels. */
    public int windowedWidth = 1080;

    /** Last known windowed height in pixels. */
    public int windowedHeight = 720;

    /** Simulation speed multiplier (1x, 2x, 4x). */
    public int gameSpeed = 1;

    /** Master volume (0.0 to 1.0). */
    /**
     * Data structure for persisting user preferences, now including separate volume levels for audio channels.
     * These fields are automatically serialized to JSON for persistent storage between sessions.
     */
    public float masterVolume = 0.85f;
    public float musicVolume = 0.4f;
    public float soundVolume = 0.85f;}

