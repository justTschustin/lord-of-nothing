package io.github.lord_of_nothing.events;

import io.github.lord_of_nothing.settings.ResolutionDto;

/**
 * Event that requests resolution adjustment
 */
@SuppressWarnings("unused")
public class ResolutionChangedEvent implements Event {
    private final ResolutionDto resolution;

    public ResolutionChangedEvent(ResolutionDto resolution) {
        this.resolution = resolution;
    }

    public ResolutionDto getResolution() {
        return resolution;
    }
}


