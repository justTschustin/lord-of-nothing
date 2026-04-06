package io.github.lord_of_nothing.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import io.github.lord_of_nothing.events.EventBus;

/**
 */
public class TextButton extends Button {

    private static final Texture WHITE_PIXEL = createWhitePixel();
    private static final GlyphLayout GLYPH_LAYOUT = new GlyphLayout();

    private final String text;
    private final BitmapFont font = new BitmapFont();

    public TextButton(
        float x,
        float y,
        float width,
        float height,
        EventBus eventBus,
        String text,
        Runnable onClick
    ) {
        super(x, y, width, height, eventBus, onClick);
        this.text = text;
    }

    public TextButton(
        float x,
        float y,
        float width,
        float height,
        EventBus eventBus,
        String text,
        Runnable onClick,
        boolean publishUiElement
    ) {
        super(x, y, width, height, eventBus, onClick, publishUiElement);
        this.text = text;
    }

    @Override
    public void render(SpriteBatch batch) {
        Color previous = batch.getColor();

        batch.setColor(0.22f, 0.22f, 0.22f, 0.95f);
        batch.draw(WHITE_PIXEL, bounds.x, bounds.y, bounds.width, bounds.height);

        GLYPH_LAYOUT.setText(font, text);
        float textX = bounds.x + (bounds.width - GLYPH_LAYOUT.width) / 2f;
        float textY = bounds.y + (bounds.height + GLYPH_LAYOUT.height) / 2f;

        batch.setColor(Color.WHITE);
        font.draw(batch, GLYPH_LAYOUT, textX, textY);
        batch.setColor(previous);
    }

    private static Texture createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
