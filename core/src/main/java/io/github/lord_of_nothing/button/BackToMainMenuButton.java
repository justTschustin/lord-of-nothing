package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.EventBus;

public class BackToMainMenuButton extends TextButton {

    public BackToMainMenuButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Main Menu", () -> eventBus.publish(new BackToMainMenuEvent()));
    }
}

