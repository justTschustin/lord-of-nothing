package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.GameSpeedChangedEvent;

/**
 * Button that publishes a {@link GameSpeedChangedEvent} for a fixed speed multiplier.
 */
public class GameSpeedButton extends TextButton {
    private final int gameSpeed;

    /**
     * Creates a speed-selection button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     * @param gameSpeed speed multiplier represented by this button
     * @param eventBus event bus used for click event publishing
     */
    public GameSpeedButton(
        float x,
        float y,
        float width,
        float height,
        int gameSpeed,
        EventBus eventBus
    ) {
        super(
            x,
            y,
            width,
            height,
            eventBus,
            buildLabel(gameSpeed, false),
            () -> eventBus.publish(new GameSpeedChangedEvent(gameSpeed))
        );
        this.gameSpeed = gameSpeed;
    }

    /**
     * Returns the speed multiplier represented by this button.
     *
     * @return speed multiplier value
     */
    public int getGameSpeed() {
        return gameSpeed;
    }

    /**
     * Updates the label so the currently active speed is visible in the top bar.
     *
     * @param selected whether this speed is currently active
     */
    public void setSelected(boolean selected) {
        setText(buildLabel(gameSpeed, selected));
    }

    private static String buildLabel(int speed, boolean selected) {
        String label = speed + "x";
        return selected ? "[" + label + "]" : label;
    }
}

