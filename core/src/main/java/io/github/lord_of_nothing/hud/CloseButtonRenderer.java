package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;

/**
 * Verantwortlich für die isolierte Darstellung des Schließen-Buttons.
 * Wird als oberste Ebene im HUD gerendert, um die Interaktion jederzeit zu ermöglichen.
 */
public class CloseButtonRenderer {
    public void render(ShapeRenderer shapeRenderer, GameWindow window) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(window.getCloseButtonX(), window.getCloseButtonY(),
            GameWindow.CLOSE_BUTTON_SIZE, GameWindow.CLOSE_BUTTON_SIZE);
        shapeRenderer.end();
    }
}
