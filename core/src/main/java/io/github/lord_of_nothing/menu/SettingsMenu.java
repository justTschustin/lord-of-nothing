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

public class SettingsMenu {
    private static final float BUTTON_WIDTH = 280f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final TextButton toggleDisplayModeButton;
    private final TextButton backButton;

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
            "Toggle Fullscreen / Windowed",
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

    public void registerUiElements(EventBus eventBus) {
        setButtonLayout();
        eventBus.publish(new UiElementCreatedEvent(toggleDisplayModeButton));
        eventBus.publish(new UiElementCreatedEvent(backButton));
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        setButtonLayout();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Settings", Gdx.graphics.getWidth() / 2f - 55f, Gdx.graphics.getHeight() / 2f + 130f);
        font.draw(
            batch,
            Gdx.graphics.isFullscreen() ? "Current mode: Fullscreen" : "Current mode: Windowed",
            Gdx.graphics.getWidth() / 2f - 115f,
            Gdx.graphics.getHeight() / 2f + 95f
        );
        toggleDisplayModeButton.render(batch);
        backButton.render(batch);
        batch.end();
    }

    public void dispose() {
        font.dispose();
    }

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



