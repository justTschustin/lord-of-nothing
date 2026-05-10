package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * Renders a blocking center-screen popup with a message and confirmation button.
 */
public class PopupOverlay {
    private static final float POPUP_WIDTH = 460f;
    private static final float POPUP_HEIGHT = 220f;
    private static final float BUTTON_WIDTH = 120f;
    private static final float BUTTON_HEIGHT = 38f;

    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout textLayout = new GlyphLayout();
    private final TextButton confirmButton;

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
        float popupY = (Gdx.graphics.getHeight() - POPUP_HEIGHT) / 2f;
        float buttonX = popupX + (POPUP_WIDTH - BUTTON_WIDTH) / 2f;
        float buttonY = popupY + 24f;

        confirmButton.setBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.95f);
        shapeRenderer.rect(popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT);

        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f);
        shapeRenderer.rect(popupX + 2f, popupY + 2f, POPUP_WIDTH - 4f, POPUP_HEIGHT - 4f);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.95f);
        shapeRenderer.rect(popupX + 4f, popupY + 4f, POPUP_WIDTH - 8f, POPUP_HEIGHT - 8f);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        textLayout.setText(font, message);
        float textX = popupX + (POPUP_WIDTH - textLayout.width) / 2f;
        float textY = popupY + POPUP_HEIGHT - 74f;
        font.draw(batch, textLayout, textX, textY);
        confirmButton.render(batch);
        batch.end();
    }

    /**
     * Disposes popup-owned font resources.
     */
    public void dispose() {
        font.dispose();
    }
}

