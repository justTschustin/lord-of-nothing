package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.ResumeGameEvent;

/**
 * Button that publishes a {@link ResumeGameEvent} when clicked.
 */
public class ResumeButton extends TextButton {

    /**
     * Creates a resume button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param w button width
     * @param h button height
     * @param eventBus event bus used to publish the click event
     */
    public ResumeButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Resume", () -> eventBus.publish(new ResumeGameEvent()));
    }
}

