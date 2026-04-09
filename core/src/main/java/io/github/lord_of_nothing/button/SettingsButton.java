package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.OpenSettingsMenuEvent;

/**
 * Button that publishes an {@link OpenSettingsMenuEvent} when clicked.
 */
@SuppressWarnings("unused")
public class SettingsButton extends TextButton {

    /**
     * Creates a settings button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param w button width
     * @param h button height
     * @param eventBus event bus used to publish the click event
     */
    public SettingsButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Settings", () -> eventBus.publish(new OpenSettingsMenuEvent()));
    }
}


