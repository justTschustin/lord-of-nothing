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
}

