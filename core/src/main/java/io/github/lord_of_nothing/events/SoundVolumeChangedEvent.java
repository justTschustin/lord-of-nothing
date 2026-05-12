package io.github.lord_of_nothing.events;

/**
 * <summary>Event published when the sound effects volume is adjusted via the UI slider.</summary>
 */
public class SoundVolumeChangedEvent implements Event {
    private final float volume;
    public SoundVolumeChangedEvent(float volume) { this.volume = Math.max(0, Math.min(1, volume)); }
    public float getVolume() { return volume; }
}
