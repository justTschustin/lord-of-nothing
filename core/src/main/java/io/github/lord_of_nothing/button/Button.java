package io.github.lord_of_nothing.button;

import com.badlogic.gdx.math.Rectangle;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * Base implementation for clickable rectangular UI buttons.
 */
public abstract class Button implements UiElement {
    protected Rectangle bounds;
    protected Runnable onClick;
    private boolean enabled = true;

    /**
     * Creates a button and automatically publishes it as a UI element.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     * @param eventBus event bus used for UI registration
     * @param onClick click callback
     */
    public Button(
            float x,
            float y,
            float width,
            float height,
            EventBus eventBus,
            Runnable onClick
    ) {
        this(x, y, width, height, eventBus, onClick, true);
    }

    /**
     * Creates a button and optionally publishes it as a UI element.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     * @param eventBus event bus used for UI registration
     * @param onClick click callback
     * @param publishUiElement whether to publish a {@link UiElementCreatedEvent}
     */
    public Button(
            float x,
            float y,
            float width,
            float height,
            EventBus eventBus,
            Runnable onClick,
            boolean publishUiElement
    ) {
        this.bounds = new Rectangle(x, y, width, height);
        this.onClick = onClick;
        if (publishUiElement && eventBus != null) {
            eventBus.publish(new UiElementCreatedEvent(this));
        }
    }

    /**
     * Updates the button bounds.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     */
    public void setBounds(
        float x,
        float y,
        float width,
        float height
    ) {
        this.bounds.set(x, y, width, height);
    }

    /**
     * Returns the current x coordinate.
     *
     * @return left x coordinate
     */
    public float getX() { return bounds.x; }

    /**
     * Returns the current y coordinate.
     *
     * @return bottom y coordinate
     */
    public float getY() { return bounds.y; }

    /**
     * Returns the current width.
     *
     * @return button width
     */
    public float getWidth() { return bounds.width; }

    /**
     * Returns the current height.
     *
     * @return button height
     */
    public float getHeight() { return bounds.height; }

    /**
     * Runs the button action when the button is enabled.
     */
    @Override
    public void onClick() {
        if (enabled && onClick != null) {
            onClick.run();
        }
    }

    /**
     * Checks whether an enabled button contains a point.
     *
     * @param x x coordinate in world space
     * @param y y coordinate in world space
     * @return {@code true} if the point is inside bounds and enabled
     */
    @Override
    public boolean contains(float x, float y) {
        return enabled && bounds.contains(x, y);
    }

    /**
     * Returns whether this button is enabled.
     *
     * @return {@code true} if enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether this button is enabled.
     *
     * @param enabled enabled state
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
