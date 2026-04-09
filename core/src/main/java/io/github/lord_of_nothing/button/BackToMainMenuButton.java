package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.EventBus;

/**
 * Button that publishes a {@link BackToMainMenuEvent} when clicked.
 */
public class BackToMainMenuButton extends TextButton {

    /**
     * Creates a back-to-main-menu button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param w button width
     * @param h button height
     * @param eventBus event bus used to publish the click event
     */
    public BackToMainMenuButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Main Menu", () -> eventBus.publish(new BackToMainMenuEvent()));
    }
}

