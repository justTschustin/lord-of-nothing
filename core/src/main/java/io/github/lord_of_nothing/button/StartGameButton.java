package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.StartGameEvent;

public class StartGameButton extends TextButton {

    public StartGameButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Start Game", () -> eventBus.publish(new StartGameEvent()));
    }
}


