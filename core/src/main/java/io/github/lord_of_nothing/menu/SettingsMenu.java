package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.events.CloseSettingsMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.ToggleFullscreenEvent;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;

/**
 * Renders and manages the settings menu UI.
 */
public class SettingsMenu {
    private static final float BUTTON_WIDTH = 280f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final TextButton toggleDisplayModeButton;
    private final TextButton backButton;

    /**
     * Creates the settings menu and its buttons.
     *
     * @param eventBus event bus used to publish UI actions
     */
    public SettingsMenu(EventBus eventBus) {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float toggleY = centerY + BUTTON_GAP / 2f;
        float backY = toggleY - BUTTON_HEIGHT - BUTTON_GAP;

        toggleDisplayModeButton = new TextButton(
            x,
            toggleY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus,
            "Display Mode: Windowed",
            () -> eventBus.publish(new ToggleFullscreenEvent()),
            false
        );
        backButton = new TextButton(
            x,
            backY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus,
            "Back",
            () -> eventBus.publish(new CloseSettingsMenuEvent()),
            false
        );
        setButtonLayout();
    }

    /**
     * Registers menu buttons as interactive UI elements.
     *
     * @param eventBus event bus used for publishing UI element creation
     */
    public void registerUiElements(EventBus eventBus) {
        setButtonLayout();
        eventBus.publish(new UiElementCreatedEvent(toggleDisplayModeButton));
        eventBus.publish(new UiElementCreatedEvent(backButton));
    }

    /**
     * Renders the settings screen and buttons.
     *
     * @param shapeRenderer shape renderer used for background
     * @param batch sprite batch used for text and buttons
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        setButtonLayout();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Settings", Gdx.graphics.getWidth() / 2f - 55f, Gdx.graphics.getHeight() / 2f + 130f);
        toggleDisplayModeButton.setText(
            Gdx.graphics.isFullscreen() ? "Display Mode: Fullscreen" : "Display Mode: Windowed"
        );
        toggleDisplayModeButton.render(batch);
        backButton.render(batch);
        batch.end();
    }

    /**
     * Disposes menu-owned rendering resources.
     */
    public void dispose() {
        font.dispose();
    }

    /**
     * Recalculates button layout based on the current window size.
     */
    private void setButtonLayout() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float toggleY = centerY + BUTTON_GAP / 2f;
        float backY = toggleY - BUTTON_HEIGHT - BUTTON_GAP;

        toggleDisplayModeButton.setBounds(x, toggleY, BUTTON_WIDTH, BUTTON_HEIGHT);
        backButton.setBounds(x, backY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
}



