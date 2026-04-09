package io.github.lord_of_nothing.events;

import io.github.lord_of_nothing.ui.UiElement;

/**
 * Event published when a clickable UI element is created and should be tracked.
 */
public final class UiElementCreatedEvent implements Event {
    private final UiElement element;

    /**
     * Creates a new UI-element-created event.
     *
     * @param element UI element that has been created
     */
    public UiElementCreatedEvent(UiElement element) {
        this.element = element;
    }

    /**
     * Returns the created UI element.
     *
     * @return created UI element instance
     */
    public UiElement getElement() {
        return element;
    }
}
