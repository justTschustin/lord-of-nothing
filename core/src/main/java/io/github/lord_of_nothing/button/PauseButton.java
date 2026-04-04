package io.github.lord_of_nothing.button;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.PauseGameEvent;

public class PauseButton extends TextButton {

    public PauseButton(
        float x,
        float y,
        float w,
        float h,
        EventBus eventBus
    ) {
        super(x, y, w, h, eventBus, "Pause",
            () -> {
                System.out.println("Button clicked");
                eventBus.publish(new PauseGameEvent());
            });
    }
}
