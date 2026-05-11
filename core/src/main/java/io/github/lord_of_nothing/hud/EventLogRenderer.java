package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import io.github.lord_of_nothing.GameWindow;

import java.util.List;

/**
 * Renders the event log as a console-like window in the bottom right corner.
 */
public class EventLogRenderer {
    private static final float TEXT_PADDING_X = 12f;
    private static final float TEXT_PADDING_Y = 18f;
    private static final float LINE_HEIGHT = 24f;
    private static final float MESSAGE_GAP = 6f;

    private final BitmapFont font = createFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
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
        float textWidth = w - 2f * TEXT_PADDING_X;
        float currentY = y + TEXT_PADDING_Y + font.getCapHeight();
        float maxY = y + h - TEXT_PADDING_Y;
        int skippedMessages = log.getScrollOffset();

        for (int index = msgs.size() - 1 - skippedMessages; index >= 0 && currentY < maxY; index--) {
            String message = "> " + msgs.get(index);
            glyphLayout.setText(font, message, font.getColor(), textWidth, Align.left, true);

            float drawY = currentY + glyphLayout.height;
            if (drawY > maxY) {
                break;
            }

            font.draw(batch, message, x + TEXT_PADDING_X, drawY, textWidth, Align.left, true);
            currentY += glyphLayout.height + MESSAGE_GAP;
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

    private static BitmapFont createFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/Fredoka-variable-font.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter params =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 18;
        params.color = Color.WHITE;
        params.borderWidth = 0.4f;

        BitmapFont generatedFont = generator.generateFont(params);
        generator.dispose();
        return generatedFont;
    }

    public void dispose() {
        font.dispose();
    }
}
