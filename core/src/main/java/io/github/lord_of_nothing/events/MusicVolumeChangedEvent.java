package io.github.lord_of_nothing.events;

/**
 * Event published when the music volume is changed via slider.
 */
public class MusicVolumeChangedEvent implements Event {
    private final float volume;

    public MusicVolumeChangedEvent(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }

    public float getVolume() {
        return volume;
    }
}
