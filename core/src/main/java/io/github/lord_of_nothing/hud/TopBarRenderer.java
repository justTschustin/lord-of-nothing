package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.button.PauseButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Übernimmt die visuelle Darstellung der Ressourcenleiste am oberen Bildschirmrand.
 * Zeichnet den Hintergrund der Bar und die aktuellen Werte aus dem ResourceManager.
 */
public class TopBarRenderer {
    private final BitmapFont font = new BitmapFont();
    private final PauseOverlay pauseOverlay = new PauseOverlay();
    private PauseButton pauseButton;

    private static final float PAUSE_BUTTON_WIDTH = 70f;
    private static final float PAUSE_BUTTON_HEIGHT = 24f;
    private static final float PAUSE_BUTTON_MARGIN = 10f;

    public void registerUiElements(GameWindow window, EventBus eventBus) {
        setPauseButtonVariables(window, eventBus);
        if (pauseButton != null) {
            eventBus.publish(new UiElementCreatedEvent(pauseButton));
        }
    }

    public void render(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        GameWindow window,
        ResourceManager resourceManager,
        EventBus eventBus,
        boolean paused
    ) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, window.getTopBarY(), Gdx.graphics.getWidth(), GameWindow.TOP_BAR_HEIGHT);
        shapeRenderer.end();

        setPauseButtonVariables(window, eventBus);
        pauseButton.setEnabled(!paused);
        pauseOverlay.setActive(paused);

        batch.begin();
        int spacing = 150;
        int i = 0;
        for (ResourceType type : ResourceType.values()) {
            font.draw(batch, type.name() + ": " + resourceManager.getAmount(type), 20 + (i * spacing), window.getTopBarY() + 25);
            i++;
        }
        pauseButton.render(batch);
        batch.end();

        if (paused) {
            pauseOverlay.render(shapeRenderer, batch, eventBus);
        }
    }

    public void invalidatePauseOverlayUiElements() {
        pauseOverlay.invalidateUiElements();
    }

    private void setPauseButtonVariables(GameWindow window, EventBus eventBus) {
        float x = Gdx.graphics.getWidth() - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        float y = window.getTopBarY() + (GameWindow.TOP_BAR_HEIGHT - PAUSE_BUTTON_HEIGHT) / 2f;

        if (pauseButton == null) {
            pauseButton = new PauseButton(x, y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, eventBus);
            return;
        }

        pauseButton.setBounds(x, y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
    }

    public void dispose() { font.dispose(); }
}
