package io.github.lord_of_nothing.events;

/**
 * Event that requests resolution adjustment
 */
@SuppressWarnings("unused")
public class ResolutionChangedEvent implements Event {
    private final String resolution;

    public ResolutionChangedEvent(String resolution) {
        this.resolution = resolution;
    }

    public String getResolution() {
        return resolution;
    }
}


