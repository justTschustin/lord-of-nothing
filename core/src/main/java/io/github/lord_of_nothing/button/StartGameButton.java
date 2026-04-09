package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.StartGameEvent;

/**
 * Button that publishes a {@link StartGameEvent} when clicked.
 */
public class StartGameButton extends TextButton {

    /**
     * Creates a start-game button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param w button width
     * @param h button height
     * @param eventBus event bus used to publish the click event
     */
    public StartGameButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Start Game", () -> eventBus.publish(new StartGameEvent()));
    }
}


