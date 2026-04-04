package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.ResumeGameEvent;

public class ResumeButton extends TextButton {

    public ResumeButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Resume", () -> eventBus.publish(new ResumeGameEvent()));
    }
}

