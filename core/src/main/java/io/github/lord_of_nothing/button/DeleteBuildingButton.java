package io.github.lord_of_nothing.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.events.EventBus;

/**
 * Renders demolish button within inspector.
 */
public class DeleteBuildingButton extends Button {
    private final BitmapFont font = new BitmapFont();

    public DeleteBuildingButton (float x, float y, float w, float h, EventBus eventBus, Runnable onDelete) {
        super(x, y, w, h, eventBus, onDelete, true);
        setEnabled(false); // disabled when inspector not active
    }

    /**
     * Renders red button background, label, and refund warning text.
     */
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        // Red background rectangle
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.RED);
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        sr.end();

        // Button label and refund warning
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Demolish", bounds.x + 8, bounds.y + bounds.height - 8);
        font.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
        font.draw(batch, "Refunds only 50% of the resources.", bounds.x + 2, bounds.y - 6);
        batch.end();
    }


    @Override
    public void render(SpriteBatch batch) {
        // unused — drawing done via render(sr, batch) from TileInspectorRenderer
    }

    public void dispose() { font.dispose(); }
}
