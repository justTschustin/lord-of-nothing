package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;

import java.util.List;

/**
 * <summary>Renders the event log as a console-like window in the bottom right corner.</summary>
 * <remarks>Uses ShapeRenderer for the background and border until the ornate frame logic is merged.</remarks>
 */
public class EventLogRenderer {
    private final BitmapFont font = new BitmapFont();

    public void render(ShapeRenderer sr, SpriteBatch batch, GameWindow window, EventLog log, boolean paused) {
        float x = window.getRightMarginX();
        float y = window.getOffsetY();
        float w = window.getRightMarginWidth();
        float h = window.getInfoPanelY() - window.getOffsetY() - 10;

        float dimAlpha = paused ? 0.4f : 1.0f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw background box
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.1f, 0.1f, 0.1f, 0.8f * dimAlpha);
        sr.rect(x, y, w, h);
        sr.end();

        // Draw simple border
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, dimAlpha);
        sr.rect(x, y, w, h);
        sr.end();

        // Draw text
        batch.begin();
        font.setColor(1f, 1f, 1f, dimAlpha);
        List<String> msgs = log.getMessages();
        float startY = window.getOffsetY() + 20;
        int visibleRows = 8;

        for (int i = 0; i < visibleRows; i++) {
            int index = msgs.size() - 1 - log.getScrollOffset() - i;

            if (index >= 0 && index < msgs.size()) {
                font.draw(batch, "> " + msgs.get(index), window.getRightMarginX() + 10, startY);
                startY += 20;
            }
        }
        font.setColor(Color.WHITE);
        batch.end();
    }

    public void dispose() {
        font.dispose();
    }
}
