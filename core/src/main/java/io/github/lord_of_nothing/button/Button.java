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

    public Button(
            float x,
            float y,
            float width,
            float height,
            EventBus eventBus,
            Runnable onClick
    ) {
        this.bounds = new Rectangle(x, y, width, height);
        this.onClick = onClick;
        if (eventBus != null) {
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

    @Override
    public void onClick() {
        if (onClick != null) {
            onClick.run();
        }
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
}
