package io.github.lord_of_nothing.button;

import com.badlogic.gdx.Gdx;
import io.github.lord_of_nothing.events.EventBus;

/**
 * Button that exits the application when clicked.
 */
public class ExitButton extends TextButton {

    /**
     * Creates an exit button.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param w button width
     * @param h button height
     * @param eventBus event bus used for UI registration
     */
    public ExitButton(float x, float y, float w, float h, EventBus eventBus) {
        super(x, y, w, h, eventBus, "Exit", () -> Gdx.app.exit());
    }
}

