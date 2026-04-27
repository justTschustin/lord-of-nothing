package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.GameSpeedChangedEvent;

/**
 * Button that sets simulation speed to 0 (pause time) without opening the pause menu.
 */
public class TimeProgressionToggleButton extends TextButton {
    private static final String LABEL = "0x";
    private static final String SELECTED_LABEL = "[0x]";

    /**
     * Creates a time progression toggle button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     * @param eventBus event bus used for click event publishing
     */
    public TimeProgressionToggleButton(
        float x,
        float y,
        float width,
        float height,
        EventBus eventBus
    ) {
        super(x, y, width, height, eventBus, LABEL, () -> eventBus.publish(new GameSpeedChangedEvent(0)));
    }

    /**
     * Highlights the button when time progression is currently paused.
     *
     * @param selected whether speed 0 is active
     */
    public void setSelected(boolean selected) {
        setText(selected ? SELECTED_LABEL : LABEL);
    }
}


