package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;

import java.util.List;

/**
 * Renders the event log as a console-like window in the bottom right corner.
 */
public class EventLogRenderer {
    private final BitmapFont font = new BitmapFont();
    private Texture bgTexture, cornerTexture, edgeTexture;

    public void loadAssets(Texture bg, Texture corner, Texture edge) {
        this.bgTexture = bg;
        this.cornerTexture = corner;
        this.edgeTexture = edge;
        this.bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.edgeTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void render(ShapeRenderer sr, SpriteBatch batch, GameWindow window, EventLog log, boolean paused) {
        float x = window.getRightMarginX();
        float y = window.getOffsetY();
        float w = window.getRightMarginWidth();
        float h = window.getInfoPanelY() - window.getOffsetY() - 10;

        float dimAlpha = paused ? 0.4f : 1.0f;

        // Draw dark background first
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.15f, 0.15f, 0.85f * dimAlpha);
        sr.rect(x, y, w, h);
        sr.end();

        // Draw ornate frame on top (if assets loaded)
        if (bgTexture != null) {
            batch.begin();
            batch.setColor(1f, 1f, 1f, dimAlpha);
            renderDecoratedFrameAt(batch, x, y, w, h, 20f);
            batch.setColor(Color.WHITE);
            batch.end();
        }

        // Draw text
        batch.begin();
        font.setColor(1f, 1f, 1f, dimAlpha);
        List<String> msgs = log.getMessages();
        float startY = window.getOffsetY() + 20;
        int visibleRows = 8;

        for (int i = 0; i < visibleRows; i++) {
            int index = msgs.size() - 1 - log.getScrollOffset() - i;
            if (index >= 0 && index < msgs.size()) {
                font.draw(batch, "> " + msgs.get(index), x + 10, startY);
                startY += 20;
            }
        }
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void renderDecoratedFrameAt(SpriteBatch batch, float x, float y, float w, float h, float cSize) {
        float eH = 8f;

        batch.draw(bgTexture, x, y, w, h, 0, 0, (int) w, (int) h, false, false);

        int edgeTexH = edgeTexture.getHeight();
        batch.draw(edgeTexture, x + cSize, y + h - eH, w - 2 * cSize, eH, 0, 0, (int) (w - 2 * cSize), edgeTexH, false, false);
        batch.draw(edgeTexture, x + cSize, y, w - 2 * cSize, eH, 0, 0, (int) (w - 2 * cSize), edgeTexH, false, true);
        batch.draw(edgeTexture, x, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexH, (int) (h - 2 * cSize), false, false);
        batch.draw(edgeTexture, x + w - eH, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexH, (int) (h - 2 * cSize), true, false);

        int sW = cornerTexture.getWidth(), sH = cornerTexture.getHeight();
        batch.draw(cornerTexture, x, y + h - cSize, cSize, cSize, 0, 0, sW, sH, false, false);
        batch.draw(cornerTexture, x + w - cSize, y + h - cSize, cSize, cSize, 0, 0, sW, sH, true, false);
        batch.draw(cornerTexture, x + w - cSize, y, cSize, cSize, 0, 0, sW, sH, true, true);
        batch.draw(cornerTexture, x, y, cSize, cSize, 0, 0, sW, sH, false, true);
    }

    public void dispose() {
        font.dispose();
    }
}
