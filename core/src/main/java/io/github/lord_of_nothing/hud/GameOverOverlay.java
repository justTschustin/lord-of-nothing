package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * Renders a modal game-over overlay with a return-to-menu action.
 */
public class GameOverOverlay {
    private static final float PANEL_WIDTH = 540f;
    private static final float PANEL_HEIGHT = 260f;
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 42f;

    private final BitmapFont titleFont = new BitmapFont();
    private final BitmapFont bodyFont = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final TextButton backToMenuButton;
    private boolean visible;

    /**
     * Creates the overlay and wires the back button to the main-menu event.
     *
     * @param eventBus event bus used to publish the back-to-menu request
     */
    public GameOverOverlay(EventBus eventBus) {
        backToMenuButton = new TextButton(
            0f,
            0f,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            null,
            "Back to Main Menu",
            () -> eventBus.publish(new BackToMainMenuEvent()),
            false
        );
        backToMenuButton.setEnabled(false);
    }

    /**
     * Shows the game-over modal.
     */
    public void show() {
        visible = true;
        backToMenuButton.setEnabled(true);
    }

    /**
     * Hides the game-over modal.
     */
    public void hide() {
        visible = false;
        backToMenuButton.setEnabled(false);
    }

    /**
     * Returns the modal button used for exclusive input routing.
     *
     * @return back-to-menu button
     */
    public UiElement getBackToMenuButton() {
        return backToMenuButton;
    }

    /**
     * Draws the overlay and button when visible.
     *
     * @param shapeRenderer shape renderer for panel/background
     * @param batch sprite batch for text and button
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if (!visible) {
            return;
        }

        float panelX = (Gdx.graphics.getWidth() - PANEL_WIDTH) / 2f;
        float panelY = (Gdx.graphics.getHeight() - PANEL_HEIGHT) / 2f;
        float buttonX = panelX + (PANEL_WIDTH - BUTTON_WIDTH) / 2f;
        float buttonY = panelY + 28f;
        backToMenuButton.setBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.68f);
        shapeRenderer.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer.setColor(0.14f, 0.06f, 0.06f, 0.98f);
        shapeRenderer.rect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        shapeRenderer.setColor(0.78f, 0.18f, 0.18f, 1f);
        shapeRenderer.rect(panelX + 2f, panelY + PANEL_HEIGHT - 56f, PANEL_WIDTH - 4f, 2f);
        shapeRenderer.end();

        batch.begin();
        titleFont.setColor(Color.SCARLET);
        glyphLayout.setText(titleFont, "GAME OVER");
        float titleX = panelX + (PANEL_WIDTH - glyphLayout.width) / 2f;
        float titleY = panelY + PANEL_HEIGHT - 28f;
        titleFont.draw(batch, glyphLayout, titleX, titleY);

        bodyFont.setColor(Color.WHITE);
        glyphLayout.setText(bodyFont, "The bandits defeated your defenders.");
        float bodyX = panelX + (PANEL_WIDTH - glyphLayout.width) / 2f;
        float bodyY = panelY + PANEL_HEIGHT - 98f;
        bodyFont.draw(batch, glyphLayout, bodyX, bodyY);

        glyphLayout.setText(bodyFont, "Your save has been cleared.");
        float subBodyX = panelX + (PANEL_WIDTH - glyphLayout.width) / 2f;
        float subBodyY = bodyY - 28f;
        bodyFont.draw(batch, glyphLayout, subBodyX, subBodyY);

        backToMenuButton.render(batch);
        batch.end();
    }

    /**
     * Disposes overlay-owned font resources.
     */
    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
    }
}

