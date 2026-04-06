package io.github.lord_of_nothing.button;

import com.badlogic.gdx.math.Rectangle;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * Base abstract button
 */
public abstract class Button implements UiElement {
    protected Rectangle bounds;
    protected Runnable onClick;
    private boolean enabled = true;

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

    public void setBounds(
        float x,
        float y,
        float width,
        float height
    ) {
        this.bounds.set(x, y, width, height);
    }

    public float getX() { return bounds.x; }
    public float getY() { return bounds.y; }
    public float getWidth() { return bounds.width; }
    public float getHeight() { return bounds.height; }

    @Override
    public void onClick() {
        if (enabled && onClick != null) {
            onClick.run();
        }
    }

    @Override
    public boolean contains(float x, float y) {
        return enabled && bounds.contains(x, y);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
