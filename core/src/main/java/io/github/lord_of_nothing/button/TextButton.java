package io.github.lord_of_nothing.button;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.lord_of_nothing.events.EventBus;

/**
 */
public class TextButton extends Button {

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

    @Override
    public void render(SpriteBatch batch) {
        font.draw(batch, text,
            bounds.x + bounds.width / 4f,
            bounds.y + bounds.height / 2f
        );
    }
}
