package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.PauseGameEvent;

/**
 * Button that publishes a {@link PauseGameEvent} when clicked.
 */
public class PauseButton extends TextButton {

    /**
     * Creates a pause button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param w button width
     * @param h button height
     * @param eventBus event bus used to publish the click event
     */
    public PauseButton(
        float x,
        float y,
        float w,
        float h,
        EventBus eventBus
    ) {
        super(x, y, w, h, eventBus, "Pause", () -> eventBus.publish(new PauseGameEvent()));
    }
}
