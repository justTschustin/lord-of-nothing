package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;

/**
 * <summary>Renders the event log as a console-like window in the bottom right corner.</summary>
 * <remarks>Uses ShapeRenderer for the background and border until the ornate frame logic is merged.</remarks>
 */
public class EventLogRenderer {
    private final BitmapFont font = new BitmapFont();

    public void render(ShapeRenderer sr, SpriteBatch batch, GameWindow window, EventLog log) {
        float x = window.getRightMarginX();
        float y = window.getOffsetY();
        float w = window.getRightMarginWidth();
        float h = window.getInfoPanelY() - window.getOffsetY() - 10;

        // Draw background box
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        sr.rect(x, y, w, h);
        sr.end();

        // Draw simple border
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GRAY);
        sr.rect(x, y, w, h);
        sr.end();

        // Draw text
        batch.begin();
        font.setColor(Color.WHITE);
        float textY = y + h - 15;
        for (String msg : log.getMessages()) {
            font.draw(batch, "> " + msg, x + 10, textY);
            textY -= 20;
        }
        batch.end();
    }

    public void dispose() {
        font.dispose();
    }
}
