package io.github.lord_of_nothing.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renders demolish button within inspector
 * Calls onDelete when clicked.
 */
public class DeleteBuildingButton {

    private float x, y, width, height;
    private final Runnable onDelete;
    private final BitmapFont font = new BitmapFont();

    public DeleteBuildingButton(float x, float y, float w, float h, Runnable onDelete) {
        this.x = x;  this.y = y;
        this.width = w;  this.height = h;
        this.onDelete = onDelete;
    }

    /**
     * Call after a window resize so the button stays positioned correctly.
     */
    public void setBounds(float x, float y, float w, float h) {
        this.x = x;  this.y = y;  this.width = w;  this.height = h;
    }

    /**
     * Renders red button background, label, and refund warning text.
     * ShapeRenderer and SpriteBatch must NOT be open when this is called...
     * the method opens and closes them itself.
     */
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        // Red background rectangle
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.RED);
        sr.rect(x, y, width, height);
        sr.end();

        // Button label and refund warning
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Demolish", x + 8, y + height - 8);
        font.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
        font.draw(batch, "Refunds only 50%",    x, y - 6);
        font.draw(batch, "of the resources.",    x, y - 22);
        batch.end();
    }

    /**
     * Tests whether world-space coordinates fall inside this button.
     * If yes, fires the onDelete callback and returns true.
     *
     * @param worldX x in world/camera coordinates (NOT raw screen X)
     * @param worldY y in world/camera coordinates (NOT raw screen Y)
     */
    public boolean handleClick(float worldX, float worldY) {
        if (worldX >= x && worldX <= x + width &&
            worldY >= y && worldY <= y + height) {
            onDelete.run();
            return true;
        }
        return false;
    }

    public float getX()      { return x; }
    public float getY()      { return y; }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }

    public void dispose() { font.dispose(); }
}
