package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * Renders a blocking center-screen popup with a message and confirmation button.
 */
public class PopupOverlay {
    private static final float POPUP_WIDTH = 460f;
    private static final float POPUP_MIN_HEIGHT = 220f;
    private static final float POPUP_TEXT_PADDING_X = 32f;
    private static final float POPUP_TOP_PADDING = 28f;
    private static final float POPUP_BOTTOM_PADDING = 24f;
    private static final float POPUP_TEXT_BUTTON_GAP = 24f;
    private static final float POPUP_TEXT_WIDTH = POPUP_WIDTH - (POPUP_TEXT_PADDING_X * 2f);
    private static final float BUTTON_WIDTH = 120f;
    private static final float BUTTON_HEIGHT = 38f;
    private static final float FRAME_CORNER_SIZE = 28f;
    private static final float FRAME_EDGE_SIZE = 8f;

    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout textLayout = new GlyphLayout();
    private final TextButton confirmButton;

    private Texture bgTexture;
    private Texture cornerTexture;
    private Texture edgeTexture;
    private boolean visible;
    private String message = "";

    /**
     * Creates a popup overlay with a dismiss callback.
     *
     * @param onDismiss callback invoked when the confirmation button is clicked
     */
    public PopupOverlay(Runnable onDismiss) {
        confirmButton = new TextButton(0f, 0f, BUTTON_WIDTH, BUTTON_HEIGHT, null, "OK", onDismiss, false);
        confirmButton.setEnabled(false);
    }

    /**
     * Shows the popup with the given text.
     *
     * @param message message to display in the popup
     */
    public void show(String message) {
        this.message = message == null ? "" : message;
        visible = true;
        confirmButton.setEnabled(true);
    }

    /**
     * Loads the shared HUD textures used for the popup frame.
     *
     * @param bg background texture for the panel interior
     * @param corner corner texture for the frame
     * @param edge edge texture for the frame borders
     */
    public void loadAssets(Texture bg, Texture corner, Texture edge) {
        this.bgTexture = bg;
        this.cornerTexture = corner;
        this.edgeTexture = edge;
        if (this.bgTexture != null) {
            this.bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }
        if (this.edgeTexture != null) {
            this.edgeTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }
    }

    /**
     * Hides the popup.
     */
    public void hide() {
        visible = false;
        confirmButton.setEnabled(false);
    }

    /**
     * Returns whether the popup is currently visible.
     *
     * @return {@code true} when visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns the popup confirmation button as the active modal UI element.
     *
     * @return popup confirmation button
     */
    public UiElement getConfirmButton() {
        return confirmButton;
    }

    /**
     * Draws the popup backdrop, panel, text, and button.
     *
     * @param shapeRenderer shape renderer for background rectangles
     * @param batch sprite batch for text/button rendering
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if (!visible) {
            return;
        }

        float popupX = (Gdx.graphics.getWidth() - POPUP_WIDTH) / 2f;
        font.setColor(Color.WHITE);
        textLayout.setText(font, message, Color.WHITE, POPUP_TEXT_WIDTH, Align.center, true);

        float popupHeight = Math.max(
            POPUP_MIN_HEIGHT,
            POPUP_TOP_PADDING + textLayout.height + POPUP_TEXT_BUTTON_GAP + BUTTON_HEIGHT + POPUP_BOTTOM_PADDING
        );
        float popupY = (Gdx.graphics.getHeight() - popupHeight) / 2f;
        float buttonX = popupX + (POPUP_WIDTH - BUTTON_WIDTH) / 2f;
        float buttonY = popupY + POPUP_BOTTOM_PADDING;

        confirmButton.setBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        if (bgTexture != null && cornerTexture != null && edgeTexture != null) {
            renderDecoratedFrameAt(batch, popupX, popupY, popupHeight);
        }

        float textX = popupX + POPUP_TEXT_PADDING_X;
        float textY = popupY + popupHeight - POPUP_TOP_PADDING;
        font.draw(batch, textLayout, textX, textY);
        confirmButton.render(batch);
        batch.end();
    }

    private void renderDecoratedFrameAt(SpriteBatch batch, float x, float y, float h) {
        batch.draw(bgTexture, x, y, POPUP_WIDTH, h, 0, 0, (int) POPUP_WIDTH, (int) h, false, false);

        int edgeTexH = edgeTexture.getHeight();
        batch.draw(edgeTexture, x + FRAME_CORNER_SIZE, y + h - FRAME_EDGE_SIZE, POPUP_WIDTH - 2 * FRAME_CORNER_SIZE, FRAME_EDGE_SIZE,
            0, 0, (int) (POPUP_WIDTH - 2 * FRAME_CORNER_SIZE), edgeTexH, false, false);
        batch.draw(edgeTexture, x + FRAME_CORNER_SIZE, y, POPUP_WIDTH - 2 * FRAME_CORNER_SIZE, FRAME_EDGE_SIZE,
            0, 0, (int) (POPUP_WIDTH - 2 * FRAME_CORNER_SIZE), edgeTexH, false, true);
        batch.draw(edgeTexture, x, y + FRAME_CORNER_SIZE, FRAME_EDGE_SIZE, h - 2 * FRAME_CORNER_SIZE,
            0, 0, edgeTexH, (int) (h - 2 * FRAME_CORNER_SIZE), false, false);
        batch.draw(edgeTexture, x + POPUP_WIDTH - FRAME_EDGE_SIZE, y + FRAME_CORNER_SIZE, FRAME_EDGE_SIZE, h - 2 * FRAME_CORNER_SIZE,
            0, 0, edgeTexH, (int) (h - 2 * FRAME_CORNER_SIZE), true, false);

        int cornerWidth = cornerTexture.getWidth();
        int cornerHeight = cornerTexture.getHeight();
        batch.draw(cornerTexture, x, y + h - FRAME_CORNER_SIZE, FRAME_CORNER_SIZE, FRAME_CORNER_SIZE, 0, 0, cornerWidth, cornerHeight, false, false);
        batch.draw(cornerTexture, x + POPUP_WIDTH - FRAME_CORNER_SIZE, y + h - FRAME_CORNER_SIZE, FRAME_CORNER_SIZE, FRAME_CORNER_SIZE, 0, 0, cornerWidth, cornerHeight, true, false);
        batch.draw(cornerTexture, x + POPUP_WIDTH - FRAME_CORNER_SIZE, y, FRAME_CORNER_SIZE, FRAME_CORNER_SIZE, 0, 0, cornerWidth, cornerHeight, true, true);
        batch.draw(cornerTexture, x, y, FRAME_CORNER_SIZE, FRAME_CORNER_SIZE, 0, 0, cornerWidth, cornerHeight, false, true);
    }

    /**
     * Disposes popup-owned font resources.
     */
    public void dispose() {
        font.dispose();
    }
}

