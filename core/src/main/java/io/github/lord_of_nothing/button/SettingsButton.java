package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.OpenSettingsMenuEvent;

@SuppressWarnings("unused")
public class SettingsButton extends TextButton {

    public SettingsButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Settings", () -> eventBus.publish(new OpenSettingsMenuEvent()));
    }
}


