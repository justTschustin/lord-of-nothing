package io.github.lord_of_nothing.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import io.github.lord_of_nothing.events.EventBus;

/**
 * Button implementation that draws a text label on a colored rectangle.
 */
public class TextButton extends Button {

    private static final Texture WHITE_PIXEL = createWhitePixel();
    private static final GlyphLayout GLYPH_LAYOUT = new GlyphLayout();

    private String text;
    private final BitmapFont font = createFont();
    /**
     * Creates a text button and publishes it as a UI element.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     * @param eventBus event bus used for UI registration
     * @param text button label
     * @param onClick click callback
     */
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

    /**
     * Updates the visible button label.
     *
     * @param text label text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Creates a text button and optionally publishes it as a UI element.
     *
     * @param x left x coordinate
     * @param y bottom y coordinate
     * @param width button width
     * @param height button height
     * @param eventBus event bus used for UI registration
     * @param text button label
     * @param onClick click callback
     * @param publishUiElement whether to publish a UI creation event
     */
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

    /**
     * Renders the button background and centered label.
     *
     * @param batch sprite batch used for rendering
     */
    @Override
    public void render(SpriteBatch batch) {
        Color previous = batch.getColor();

        batch.setColor(0.98f, 0.98f, 0.98f, 1f);
        batch.draw(WHITE_PIXEL, bounds.x, bounds.y, bounds.width, bounds.height);

        GLYPH_LAYOUT.setText(font, text);
        float textX = bounds.x + (bounds.width - GLYPH_LAYOUT.width) / 2f;
        float textY = bounds.y + (bounds.height + GLYPH_LAYOUT.height) / 2f;

        batch.setColor(Color.WHITE);
        font.draw(batch, GLYPH_LAYOUT, textX, textY);
        batch.setColor(previous);
    }

    /**
     * Creates a one-pixel white texture used for rectangle rendering.
     *
     * @return shared white pixel texture
     */
    private static Texture createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private static BitmapFont createFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/Fredoka-variable-font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 24;
        params.color = Color.WHITE;
        params.borderWidth = 0.7f;

        BitmapFont f = generator.generateFont(params);
        generator.dispose();
        return f;
    }
}
