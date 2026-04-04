package io.github.lord_of_nothing.events;

import io.github.lord_of_nothing.ui.UiElement;

public final class UiElementCreatedEvent implements Event {
    private final UiElement element;

    public UiElementCreatedEvent(UiElement element) {
        this.element = element;
    }

    public UiElement getElement() {
        return element;
    }
}
