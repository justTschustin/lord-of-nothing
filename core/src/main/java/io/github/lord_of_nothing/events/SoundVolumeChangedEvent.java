package io.github.lord_of_nothing.events;

/**
 * Event published when the sound effects volume is adjusted via the UI slider.
 */
public class SoundVolumeChangedEvent implements Event {
    private final float volume;
    public SoundVolumeChangedEvent(float volume) { this.volume = Math.max(0, Math.min(1, volume)); }
    public float getVolume() { return volume; }
}
