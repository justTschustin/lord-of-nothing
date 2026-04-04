package io.github.lord_of_nothing.button;

import com.badlogic.gdx.Gdx;
import io.github.lord_of_nothing.events.EventBus;

public class ExitButton extends TextButton {

    public ExitButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Exit", () -> Gdx.app.exit());
    }
}

